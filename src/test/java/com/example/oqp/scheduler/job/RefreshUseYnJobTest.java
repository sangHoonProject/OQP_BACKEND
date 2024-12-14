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
class RefreshUseYnJobTest {

    @Qualifier("refreshTokenUseYnJob")
    @Autowired
    Job job;

    @Autowired
    JobLauncher jobLauncher;

    @Test
    @DisplayName("refresh 토큰 만료 기간 테스트")
    void refreshUseYnJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobExecution ex = jobLauncher.run(job, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, ex.getExitStatus());
    }
}