package org.edgegallery.atp.application.task;

import org.edgegallery.atp.repository.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
@EnableAsync
class TestSchedule {

    @Autowired
    TaskRepository taskRepository;

    @Scheduled(cron = "0/30 * *  * * ? ")
    public void startSchedule() {
        System.out.println("===========1=>");
        try {
            taskRepository.queryAllRunningTasks();
            for (int i = 1; i <= 10; i++) {
                System.out.println("=1==>" + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
