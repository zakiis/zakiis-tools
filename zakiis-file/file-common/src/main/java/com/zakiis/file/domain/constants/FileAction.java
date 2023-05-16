package com.zakiis.file.domain.constants;

import java.util.Arrays;
import java.util.List;

public enum FileAction {

	UPLOADING,
	PHASE_MOVING_INIT,
	PHASE_MOVING_NEW_FILE_COPIED,
	PHASE_MOVING_PATH_CHANGED,
	PHASE_MOVING_OLD_FILE_DELETED,
	COMPLETE,
	;
	
	public static List<FileAction> phaseMovingAction() {
		return Arrays.asList(PHASE_MOVING_INIT, PHASE_MOVING_NEW_FILE_COPIED
				, PHASE_MOVING_OLD_FILE_DELETED, PHASE_MOVING_PATH_CHANGED);
	}
}
