CREATE TABLE notifications (
    id                 BIGSERIAL     PRIMARY KEY,
    recipient_id       BIGINT        NOT NULL,
    notification_type  VARCHAR(100)  NOT NULL,
    channel            VARCHAR(50)   NOT NULL,
    reference_type     VARCHAR(100)  NOT NULL,
    reference_id       VARCHAR(255)  NOT NULL,
    deduplication_key  VARCHAR(500)  NOT NULL,
    title              VARCHAR(500)  NOT NULL,
    content            TEXT          NOT NULL,
    status             VARCHAR(50)   NOT NULL DEFAULT 'READY',
    read_status        VARCHAR(50)   NOT NULL DEFAULT 'UNREAD',
    retry_count        INT           NOT NULL DEFAULT 0,
    max_retry_count    INT           NOT NULL DEFAULT 3,
    scheduled_at       TIMESTAMP     NOT NULL,
    next_retry_at      TIMESTAMP     NOT NULL,
    locked_by          VARCHAR(255),
    locked_at          TIMESTAMP,
    sent_at            TIMESTAMP,
    read_at            TIMESTAMP,
    failed_at          TIMESTAMP,
    failure_reason     TEXT,
    manual_retry_count INT           NOT NULL DEFAULT 0,
    created_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_notifications_deduplication_key UNIQUE (deduplication_key)
);

CREATE INDEX idx_notifications_status_scheduled
    ON notifications (status, scheduled_at, next_retry_at);
CREATE INDEX idx_notifications_recipient_id
    ON notifications (recipient_id);
CREATE INDEX idx_notifications_processing_locked
    ON notifications (status, locked_at)
    WHERE status = 'PROCESSING';

CREATE TABLE notification_templates (
    id                 BIGSERIAL    PRIMARY KEY,
    notification_type  VARCHAR(100) NOT NULL,
    channel            VARCHAR(50)  NOT NULL,
    title_template     VARCHAR(500) NOT NULL,
    content_template   TEXT         NOT NULL,
    enabled            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_templates_type_channel UNIQUE (notification_type, channel)
);

INSERT INTO notification_templates (notification_type, channel, title_template, content_template) VALUES
('COURSE_ENROLLMENT_COMPLETED', 'EMAIL',
 '{courseName} 수강 신청이 완료되었습니다',
 '안녕하세요. {courseName} 수강 신청이 완료되었습니다. 열심히 수강하세요!'),
('COURSE_ENROLLMENT_COMPLETED', 'IN_APP',
 '{courseName} 수강 신청 완료',
 '{courseName} 수강 신청이 완료되었습니다.'),
('PAYMENT_CONFIRMED', 'EMAIL',
 '결제가 확정되었습니다',
 '{courseName} 강의 {amount}원 결제가 확정되었습니다.'),
('PAYMENT_CONFIRMED', 'IN_APP',
 '결제 확정',
 '{courseName} {amount}원 결제가 확정되었습니다.'),
('LECTURE_START_D1', 'EMAIL',
 '{courseName} 강의 시작 D-1 안내',
 '{courseName} 강의가 내일({startDate}) 시작됩니다. 준비되셨나요?'),
('LECTURE_START_D1', 'IN_APP',
 '{courseName} 강의 D-1',
 '{courseName} 강의가 내일({startDate}) 시작됩니다.'),
('COURSE_CANCELED', 'EMAIL',
 '{courseName} 강의가 취소되었습니다',
 '{courseName} 강의가 취소되었습니다. 환불은 영업일 기준 3-5일 내 처리됩니다.'),
('COURSE_CANCELED', 'IN_APP',
 '{courseName} 강의 취소',
 '{courseName} 강의가 취소되었습니다.');
