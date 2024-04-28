package esgi.codelink.dto;

public class RegisterDTO {

    private String mail;
    private String password;
    private String passwordCheck;

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }


        public void setMail(String mail) {
            this.mail = mail;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPasswordCheck() {
            return passwordCheck;
        }

        public void setPasswordCheck(String passwordCheck) {
            this.passwordCheck = passwordCheck;
        }
    }

