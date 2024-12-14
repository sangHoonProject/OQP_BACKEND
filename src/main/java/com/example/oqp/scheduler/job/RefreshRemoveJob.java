package com.example.oqp.scheduler.job;

import com.example.oqp.db.entity.JwtRefresh;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class RefreshRemoveJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean("refreshTokenStatusRemoveJob")
    public Job refreshRemoveJob(@Qualifier("deleteRefreshStep") Step deleteRefreshStep) {
        return new JobBuilder("refreshRemoveJob", jobRepository)
                .start(deleteRefreshStep)
                .build();
    }

    @Bean("deleteRefreshStep")
    public Step deleteRefreshStep(@Qualifier("refreshItemReader") JpaPagingItemReader<JwtRefresh> refreshItemReader){
        return new StepBuilder("refreshRemoveStep", jobRepository)
                .<JwtRefresh, JwtRefresh>chunk(100, transactionManager)
                .reader(refreshItemReader)
                .processor(refreshChangeNProcessor())
                .writer(refreshUpdateWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @StepScope
    @Bean("refreshItemReader")
    public JpaPagingItemReader<JwtRefresh> refreshRead(){
        JpaPagingItemReader<JwtRefresh> reader = new JpaPagingItemReader<>();
        reader.setPageSize(100);
        reader.setQueryString("select f from JwtRefresh f where f.useYn = 'N'");
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    @StepScope
    @Bean
    public ItemProcessor<JwtRefresh, JwtRefresh> refreshChangeNProcessor(){
        return new ItemProcessor<JwtRefresh, JwtRefresh>() {

            @Override
            public JwtRefresh process(JwtRefresh item) throws Exception {
                log.info("item : {}", item.toString());

                return item;
            }
        };
    }

    @StepScope
    @Bean
    public JpaItemWriter<JwtRefresh> refreshUpdateWriter() {
        JpaItemWriter<JwtRefresh> writer = new JpaItemWriter<>(){
            @Override
            protected void doWrite(EntityManager entityManager, Chunk<? extends JwtRefresh> items) {
                for (JwtRefresh jwtRefresh : items) {
                    log.info("Deleting item: {}", jwtRefresh);

                    if (!entityManager.contains(jwtRefresh)) {
                        jwtRefresh = entityManager.merge(jwtRefresh);
                    }
                    entityManager.remove(jwtRefresh);
                }
            }
        };
        writer.setEntityManagerFactory(entityManagerFactory);

        return writer;
    }

    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("refreshRemoveJob-");
        executor.initialize();
        return executor;
    }
}
