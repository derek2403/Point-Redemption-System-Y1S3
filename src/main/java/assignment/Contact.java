package assignment;

public abstract class Contact {
    protected String name;
    protected String email;
    protected String address;
    protected char gender;
    protected String phoneNum;


    public Contact() {
        this.name = "";
        this.email = "";
        this.address = "";
        this.gender = ' ';
        this.phoneNum = "";
    }

    public Contact(String name, String email, String address, char gender, String phoneNum) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.phoneNum = phoneNum;
    }

    public abstract void register();

    public abstract void displayDetails();

    public String validateName(String name) {
        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException("Name must contain only alphabets");
        }
        return name;
    }

    public String validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
        return email;
    }

    public char validateGender(String gender) {
        if (!gender.matches("[mMfF]")) {
            throw new IllegalArgumentException("Gender must be M or F");
        }
        return Character.toUpperCase(gender.charAt(0));
    }

    public String validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("01\\d{8}|\\d{9}")) {
            throw new IllegalArgumentException("Phone number must start with 01 and be followed by 8 or 9 digits");
        }
        return phoneNumber;
    }

    public String validateAddress(String address) {
        address = address.replace(",", " ");
        return address;
    }

    public static void userMenu(){
        System.out.println(" ___ ___    ___  ___ ___  ____     ___  ____    _____");
        System.out.println("|   |   |  /  _]|   |   ||    \\   /  _]|    \\  / ___/");
        System.out.println("| _   _ | /  [_ | _   _ ||  o  ) /  [_ |  D  )(   \\_ ");
        System.out.println("|  \\_/  ||    _]|  \\_/  ||     ||    _]|    /  \\__  |");
        System.out.println("|   |   ||   [_ |   |   ||  O  ||   [_ |    \\  /  \\ |");
        System.out.println("|   |   ||     ||   |   ||     ||     ||  .  \\ \\    |");
        System.out.println("|___|___||_____||___|___||_____||_____||__|\\_|  \\___|");
        System.out.println("                                                     ");
        System.out.println("Choose the category to manage.\n1. Administrator\n2. Customer\n3. Back");
    }
}
