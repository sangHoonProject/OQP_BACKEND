package com.example.oqp.scheduler.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshRemoveJobTest {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("refreshTokenStatusRemoveJob")
    Job job;

    @Test
    @DisplayName("refresh 토큰 제거 테스트")
    void refreshRemoveJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobExecution ex = jobLauncher.run(job, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, ex.getExitStatus());
    }

}