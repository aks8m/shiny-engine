package com.github.aks8m;

import net.sf.mpxj.CustomField;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;

import java.util.*;

public class WritableTask {

    private Task task;
    private String name;
    private String startDate;
    private String finishDate;
    private String percentageComplete;
    private String notes;
    private boolean isMilestone;
    private List<Resource> resources = new ArrayList<>();
    private boolean isDeliverable;
    private Stack<String> parentTaskHierarchy = new Stack<>();
    private String resource;
    private String programName;
    private String projectName;
    private String wbs;
    private String duration;

    public WritableTask(Task task){
        this.task = task;
        this.name = "";
        this.startDate = task.getStart().toString();
        this.finishDate = task.getFinish().toString();
        this.percentageComplete = task.getPercentageComplete().toString();
        this.isMilestone = task.getMilestone();
        this.notes = task.getNotes();
        this.duration = task.getDuration().toString().replace("d","");
        this.wbs = task.getWBS();

        for (ResourceAssignment resourceAssignment : task.getResourceAssignments()) {
            Resource tempResource = resourceAssignment.getResource();
            if (tempResource != null) {
                this.resources.add(tempResource);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < this.resources.size() - 1; i++){
            stringBuilder.append(resources.get(i).getName() + " & ");
        }

        if(this.resources.size() > 1) {
            stringBuilder.append(this.resources.get(this.resources.size() - 1).getName());
        }
        this.resource = stringBuilder.toString();

        this.projectName = this.task.getName();
        this.programName = this.task.getParentTask().getName();
    }

    public WritableTask(Task task, List<CustomField> customFields){
        this.task = task;
        this.name = task.getName();
        this.startDate = task.getStart().toString();
        this.finishDate = task.getFinish().toString();
        this.percentageComplete = task.getPercentageComplete().toString();
        this.isMilestone = task.getMilestone();
        this.notes = task.getNotes();
        this.duration = task.getDuration().toString().replace("d","");
        this.wbs = task.getWBS();

        for (CustomField customField : customFields) {
            if(customField.getAlias().equals("Deliverables")){
                if (String.valueOf(this.task.getFieldByAlias(customField.getAlias())).equals("D")) {
                    this.isDeliverable = true;
                }else {
                    this.isDeliverable = false;
                }
            }
        }

        for (ResourceAssignment resourceAssignment : task.getResourceAssignments()) {
            Resource tempResource = resourceAssignment.getResource();
            if (tempResource != null) {
                this.resources.add(tempResource);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < this.resources.size() - 1; i++){
            stringBuilder.append(resources.get(i).getName() + " & ");
        }

        if(this.resources.size() > 1) {
            stringBuilder.append(this.resources.get(this.resources.size() - 1).getName());
        }
        this.resource = stringBuilder.toString();
    }

    public void buildHierarchy(){

        Task tempTask = this.task;

        while (tempTask.getParentTask() != null && !tempTask.getParentTask().getName().contains("C:")){
            this.parentTaskHierarchy.push(tempTask.getParentTask().getName());
            tempTask = tempTask.getParentTask();
        }

        if(this.parentTaskHierarchy.size() < 1){
            this.programName = "";
            this.projectName = "";
        }else if(this.parentTaskHierarchy.size() < 2){
            String programString = this.parentTaskHierarchy.pop();
            this.programName = programString;
            this.projectName = programString;
        }else{
            this.programName = this.parentTaskHierarchy.pop();
            this.projectName = this.parentTaskHierarchy.pop();
        }
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getPercentageComplete() {
        return percentageComplete;
    }

    public String getNotes() {
        return notes;
    }

    public String isMilestone() {
        return String.valueOf(this.isMilestone);
    }

    public String isDeliverable() {
        return String.valueOf(this.isDeliverable);
    }

    public String getResource() {
        return resource;
    }

    public String getProgramName() {
        return programName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getWbs() {
        return wbs;
    }

    public String getDuration() {
        return duration;
    }
}
