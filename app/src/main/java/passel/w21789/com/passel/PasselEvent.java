package passel.w21789.com.passel;

import java.util.ArrayList;

/**
 * Created by lisandro on 4/6/15.
 */
public class PasselEvent {
    private String eventName;
    private String eventTime;
    private String endTime;
    private String eventDescription;
    private String eventDate;
    private String endDate;
    private ArrayList<String> eventGuests;
    private ArrayList<Double> eventCoordinates;

    public PasselEvent(){

    }

    public PasselEvent(String eventName, String eventTime){
        this.eventName = eventName;
        this.eventTime = eventTime;
    }

    public PasselEvent(String eventName, String eventTime, ArrayList<String> eventGuests, ArrayList<Double> eventCoordinates){
        this.eventName = eventName;
        this.eventTime = eventTime;
        setEventGuests(eventGuests);
        setEventCoordinates(eventCoordinates);
    }

    public PasselEvent(String eventName, String eventTime, String endTime, ArrayList<String> eventGuests, ArrayList<Double> eventCoordinates, String eventDate, String endDate, String description){
        this.eventName = eventName;
        this.eventTime = eventTime;
        setEventGuests(eventGuests);
        setEventCoordinates(eventCoordinates);
        this.setEndTime(endTime);
        this.setEventDate(eventDate);
        this.setEndDate(endDate);
        this.eventDescription = description;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public ArrayList<String> getEventGuests() {
        return eventGuests;
    }

    public void setEventGuests(ArrayList<String> eventGuests) {
        this.eventGuests = eventGuests;
    }

    public void addGuest(String newGuest){
        eventGuests.add(newGuest);
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public ArrayList<Double> getEventCoordinates() {
        return eventCoordinates;
    }

    public void setEventCoordinates(ArrayList<Double> eventCoordinates) {
        this.eventCoordinates = eventCoordinates;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}