package tk.musicoflife.travio;

public class User {
    private String Name;
    private String Email;
    private String Mobile = "";
    private String Address = "";
    private String DOB = "";
    private String Occupation = "";
    private String Gender = "";
    private String Contact_Form = "";

    public User() {
    }

    public User(String name, String email, String mobile, String address, String DOB, String occupation, String gender, String Contact_Form) {
        Name = name;
        Email = email;
        Mobile = mobile;
        Address = address;
        this.DOB = DOB;
        Occupation = occupation;
        Gender = gender;
        this.Contact_Form = Contact_Form;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getContact_Form() {
        return Contact_Form;
    }

    public void setContact_Form(String Contact_Form) {
        this.Contact_Form = Contact_Form;
    }
}
