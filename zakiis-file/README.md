## zakiis-file

### Introduction

zakiis file is designed for file uploading and downloading. It's a light weight implementation of IOBS. the network deployment architecture shows below.

![iobs.drawio](D:\workspace\zakiis\file\readme_imgs\iobs.drawio.png)

It's developed base on Spring WebFlux framework, Services is able to handle concurrent requests in a non-blocking way which tremendously promote the ability to deal with high concurrency scenario, It consist of three parts:

- file-common: includes model class of MongoDB and some common class like  filter, exception handler and data transfer object. It's packaged as jar file and imported by file-core and file-portal.
- file-core: provides basic method of file uploading and downloading, and the ability of file lifecycle management.
- file-portal: provide the authentication and authorization mechanism. you can manage user, channel and bucket. configure the properly privileges to make request in a valid way.

### Authorization

Zakiis-file provides two mechanism of authorization:

- user authorization: for web management system user
- channel authorization: for external system that need IOBS ability.

#### User authorization

file-portal provide a simple username/password login method and generate a JWT token for the request validation. you can see [How do JSON Web Tokens work](https://jwt.io/introduction/)for detail. make sure add the header in your request after login.

```properties
Authorization: Bearer <JWT token return by login method>
```

#### Channel authorization

External system do not use username/password authorization, It should use channel mechanism  created by channel create/update method. and add correct header in every request.

```properties
X-ACCESS-KEY: <channel access key>
X-SIGN: <calculate sign of this request>
```

On get method you can also pass ak and sign parameters in the URL like `http://xxx?ak=<channel access key>&sign=<calculate sign of this request>`.

The rule of generated sign shows as following.

```shell
Hex(HMAC_SHA_256("<access key>.<request method>.<uri>"))
```

### Clean Invalid File Entity

We don't use transaction for performance reason, so there may exists file entities that in uploading status and no one channel will upload it anymore. We have a task to clean that invalid file entity.

1. add a config value in YAML file to tell us how long that file entity in uploading phase should be removed.

```yaml
file: 
  core: 
    clean-uploading-file-entity-after-seconds: 86400
```

2. add a trigger to execute this task in a fixed period. For example, you can use linux crontab to do this.

```shell
crontab -e
0 2 * * * wls81 curl -XPOT http://{ip:port}/v1/file-core/task/cleanInvalidFileEntity
```

### File lifecycle

For costing reason and big data may cause the synchronization of NAS become hard to complete. We divide file in three phase:

- hot: represents the data that may read/write frequently, better store it in SSD disk.
- warm: represents the data that may read not frequently, forbidden write, better store it in HDD disk.
- cold: represents the data that may read rarely, forbidden write, better store it in cheapest solution.

Hot phase is required cause it is the default phase for every newly uploaded file, while warm and cold phase is optional and not enabled default, you can enable it by add following configuration on YAML file, a nonpositive value means disable that phase.

```yaml
file: 
  core:
    hot-path: /wls/deploy/iobs/hot
    warm-path: /wls/deploy/iobs/warm
    cold-path: /wls/deploy/iobs/cold
    move-into-warm-phase-after-day: 30
    move-into-cold-phase-after-day: 180
```

On the configuration above, file will move to warm phase after 30 days since it uploaded, and will move to cold phase after 180 days since it uploaded.

You should switch two task on to enable this feature. For example, you can add linux crontabs to enable it.

```shell
#recommand to use root user to manage task 
30 2 * * * wls81 curl -XPOT http://{ip:port}/v1/file-core/task/transformFilePhase
30 3 * * * wls81 curl -XPOT http://{ip:port}/v1/file-core/task/movingFilePhase
```

The following diagram shows the flow of file phase moving to ensure no influencing on file downloading while moving file.

![iobs-file lifecycle.drawio](D:\workspace\zakiis\file\readme_imgs\iobs-file lifecycle.drawio.png)