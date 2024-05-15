package esgi.codelink.dto;

public class RegisterDTO {

    private String mail;
    private String password;
    private String passwordCheck;
    private String lastName;
    private String firstName;

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
    public String getLastName(){
        return lastName;
    }
    public String getFirstName(){
        return firstName;
    }


        public void setMail(String mail) {
            this.mail = mail;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public void setLastName(String lastName){ this.lastName=lastName;}
        public void setFirstName(String firstName){this.firstName=firstName;
        }

        public String getPasswordCheck() {
            return passwordCheck;
        }

        public void setPasswordCheck(String passwordCheck) {
            this.passwordCheck = passwordCheck;
        }

    @Override
    public String toString() {
        return "RegisterDTO{" +
                "mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", passwordCheck='" + passwordCheck + '\'' +
                '}';
    }
}

