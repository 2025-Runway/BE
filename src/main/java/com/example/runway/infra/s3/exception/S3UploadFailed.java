package com.example.runway.infra.s3.exception;

import com.example.runway.global.error.BaseErrorException;

public class S3UploadFailed extends BaseErrorException {
    public static final S3UploadFailed Exception = new S3UploadFailed();

    public S3UploadFailed() {
        super(S3ErrorCode.S3_UPLOAD_FAILED);
    }
}
