package controller;

import event.*;
import model.TimeSheet;
import model.WorkDay;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lech0816 on 24.08.2016.
 */
public class ControllerTest {
    @Test
    public void startWorkTest(){
        Observers observers = new Observers();
        TimeSheet timeSheet = new TimeSheet();
        Controller controller = new Controller(observers,timeSheet);
        try {
            observers.notifyObservers(new StartEvent());
            Assert.assertEquals(Controller.format.format(timeSheet.getLastWorkDay().getStart()),
                    Controller.format.format(new Date()));
        } catch (ErrorEvent errorEvent) {
            errorEvent.printStackTrace();
        }
    }

    @Test
    public void endWorkTest(){
        long start = 0;
        long end = 28800000;
        Observers observers = new Observers();
        TimeSheet timeSheet = new TimeSheet();
        Controller controller = new Controller(observers,timeSheet);
        timeSheet.add(new WorkDay(new Date(start)));
        try {
            timeSheet.getLastWorkDay().setEnd(new Date(end));
            observers.notifyObservers(new CalculateEvent());
            Assert.assertEquals(Double.compare(8.0,timeSheet.getLastWorkDay().getHoursWork()),0);
        } catch (ErrorEvent errorEvent) {
            try {
                observers.notifyObservers(errorEvent);
            } catch (ErrorEvent errorEvent1) {
                errorEvent1.printStackTrace();
            }
        }
    }

    @Test
    public void pauseWorkTest(){
        Observers observers = new Observers();
        TimeSheet timeSheet = new TimeSheet();
        Controller controller = new Controller(observers,timeSheet);
        try {
            observers.notifyObservers(new StartEvent());
            observers.notifyObservers(new PauseEvent());
        } catch (ErrorEvent errorEvent) {
            errorEvent.printStackTrace();
        }

        try {
            Assert.assertEquals(Controller.format.format(timeSheet.getLastPause()[0]),
                    Controller.format.format(new Date()));
        } catch (ErrorEvent errorEvent) {
            try {
                observers.notifyObservers(errorEvent);
            } catch (ErrorEvent errorEvent1) {
                errorEvent1.printStackTrace();
            }
        }
    }

    @Test
    public void resumeWorkTest(){
        Observers observers = new Observers();
        TimeSheet timeSheet = new TimeSheet();
        Controller controller = new Controller(observers,timeSheet);


        try {
            observers.notifyObservers(new StartEvent());
            observers.notifyObservers(new PauseEvent());
            observers.notifyObservers(new ResumeEvent());
            Date[] pause;
            pause = timeSheet.getLastPause();
            Assert.assertEquals(Controller.format.format(pause[1]),Controller.format.format(new Date()));
            Assert.assertEquals(Double.compare((pause[1].getTime()-pause[0].getTime())/60000,0),0);
        } catch (ErrorEvent errorEvent) {
            try {
                observers.notifyObservers(errorEvent);
            } catch (ErrorEvent errorEvent1) {
                errorEvent1.printStackTrace();
            }
        }

    }

    @Test
    public void calculateTest(){
        Observers observers = new Observers();
        TimeSheet timeSheet = new TimeSheet();
        Controller controller = new Controller(observers,timeSheet);
        WorkDay workDay = new WorkDay(new Date(0));
        workDay.setEnd(new Date(100*60*60*1000));
        Date[] pause = new Date[2];
        pause[0]=new Date(50*60*60*1000);
        pause[1]=new Date(60*60*60*1000);
        ArrayList<Date[]> pauses = new ArrayList<>();
        pauses.add(pause);
        workDay.setPauses(pauses);
        timeSheet.add(workDay);
        try {
            observers.notifyObservers(new CalculateEvent());
            Assert.assertEquals(Double.compare(timeSheet.getLastWorkDay().getHoursWork(),90.0),0);

        } catch (ErrorEvent errorEvent) {
            errorEvent.printStackTrace();
        }
    }
}
