import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%d{ISO8601} %highlight(%-5level) [%boldBlue(%t)] %boldYellow(%logger{1.}): %msg%n%throwable'
    }
}

appender('FILE', FileAppender) {
    file = 'logs/meu-aplicativo.log'
    encoder(PatternLayoutEncoder) {
        pattern = '%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n'
    }
}

logger('devbank', DEBUG, ['STDOUT', 'FILE'], false)

root(INFO, ['STDOUT'])