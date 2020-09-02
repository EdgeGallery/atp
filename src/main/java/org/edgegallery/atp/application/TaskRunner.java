package org.edgegallery.atp.application;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
@EnableAsync
public class TaskRunner {

    @Scheduled(cron = "0/5 * *  * * ? ")
    public void startSchedule() {
        System.out.println("===========1=>");
        try {
            for(int i=1;i<=10;i++){
                System.out.println("=1==>"+i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
