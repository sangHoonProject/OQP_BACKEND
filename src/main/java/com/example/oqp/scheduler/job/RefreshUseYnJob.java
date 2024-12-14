package com.example.oqp.scheduler.job;

import com.example.oqp.common.enums.UseYn;
import com.example.oqp.db.entity.JwtRefresh;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@EnableBatchProcessing
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshUseYnJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean("refreshTokenUseYnJob")
    public Job job(@Qualifier("refreshUseYnStep") Step step){
        return new JobBuilder("refreshUseYnJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean("refreshUseYnStep")
    public Step step(
            @Qualifier("refreshTokenUseYnReader") JpaPagingItemReader<JwtRefresh> reader,
            @Qualifier("refreshTokenUseYnProcessor") ItemProcessor<JwtRefresh, JwtRefresh> processor,
            @Qualifier("refreshTokenUseYnWriter") JpaItemWriter<JwtRefresh> writer
    ) {
        return new StepBuilder("refreshUseYnStep", jobRepository)
                .<JwtRefresh, JwtRefresh>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @StepScope
    @Bean("refreshTokenUseYnReader")
    public JpaPagingItemReader<JwtRefresh> reader(){
        Map<String, Object> now = Map.of("now", LocalDateTime.now());
        JpaPagingItemReader<JwtRefresh> reader = new JpaPagingItemReader<>();

        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(100);
        reader.setQueryString("select f from JwtRefresh f where f.expiredAt < :now order by id asc");
        reader.setParameterValues(now);
        reader.setSaveState(false);

        return reader;
    }

    @StepScope
    @Bean("refreshTokenUseYnProcessor")
    public ItemProcessor<JwtRefresh, JwtRefresh> processor(){
        return new ItemProcessor<JwtRefresh, JwtRefresh>() {

            @Override
            public JwtRefresh process(JwtRefresh item) throws Exception {
                log.info("item: {}", item);
                item.setUseYn(UseYn.N);
                return item;
            }
        };
    }

    @StepScope
    @Bean("refreshTokenUseYnWriter")
    public JpaItemWriter<JwtRefresh> writer(){
        JpaItemWriter<JwtRefresh> writer = new JpaItemWriter<>(){
            @Override
            protected void doWrite(EntityManager entityManager, Chunk<? extends JwtRefresh> items) {
                for(JwtRefresh jwtRefresh : items){
                    entityManager.merge(jwtRefresh);
                }
            }
        };

        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("TokenUseYnTaskExecutor");
        executor.setConcurrencyLimit(1);
        return executor;
    }

}
