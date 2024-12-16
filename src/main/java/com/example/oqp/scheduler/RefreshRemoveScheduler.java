package com.example.oqp.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RefreshRemoveScheduler {
    private final Job refreshRemove;
    private final Job refreshUseYn;
    private final JobLauncher jobLauncher;

    public RefreshRemoveScheduler(@Qualifier("refreshTokenStatusRemoveJob") Job refreshRemove, @Qualifier("refreshTokenUseYnJob") Job refreshUseYn, JobLauncher jobLauncher) {
        this.refreshRemove = refreshRemove;
        this.refreshUseYn = refreshUseYn;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 0 */12 * * *")
    public synchronized void removeRefresh() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(refreshRemove, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
        );
    }

    @Scheduled(cron = "0 0 * * * *")
    public synchronized void useYn() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        jobLauncher.run(refreshUseYn, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()
        );
    }
}
