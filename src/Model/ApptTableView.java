package Model;

import javafx.beans.property.*;

public class ApptTableView {

    private SimpleStringProperty id;
    private SimpleStringProperty type;
    private SimpleStringProperty title;
    private SimpleStringProperty contact;
    private SimpleStringProperty start;
    private SimpleStringProperty end;
    private SimpleStringProperty zone;
    private SimpleStringProperty location;



    public ApptTableView(){

        id = new SimpleStringProperty();
        type = new SimpleStringProperty();
        title = new SimpleStringProperty();
        contact = new SimpleStringProperty();
        start = new SimpleStringProperty();
        end = new SimpleStringProperty();
        zone = new SimpleStringProperty();
        location = new SimpleStringProperty();

    }

    public ApptTableView(SimpleStringProperty id, SimpleStringProperty type, SimpleStringProperty title, SimpleStringProperty contact, SimpleStringProperty start, SimpleStringProperty end, SimpleStringProperty location) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.contact = contact;
        this.start = start;
        this.end = end;
        //this.zone = zone;
        this.location = location;
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public StringProperty startProperty() {
        return start;
    }

    public StringProperty endProperty() {
        return end;
    }

    public StringProperty zoneProperty() {
        return zone;
    }

    public StringProperty locationProperty() {
        return location;
    }

    // observable

    // get
    public String getID() {
        return idProperty().get();
    }

    public String getType() {
        return typeProperty().get();
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public String getContact() {
        return contactProperty().get();
    }

    public String getEnd() {
        return endProperty().get();
    }

    public String getStart(){
        return startProperty().get();
    }

    public String getZone(){
        return zoneProperty().get();
    }

    public String getLocation(){
        return locationProperty().get();
    }
    //get

    //set
    public void setID(String id) {
        idProperty().set(id);
    }

    public void setType(String type) {
        typeProperty().set(type);
    }

    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public void setContact(String contact) {
        contactProperty().set(contact);
    }

    public void setEnd(String end) {
        endProperty().set(end);
    }

    public void setStart(String start) {
        startProperty().set(start);
    }

    public void setZone(String zone) {
        zoneProperty().set(zone);
    }

    public void setLocation(String location) {
        locationProperty().set(location);
    }
    //set

}
