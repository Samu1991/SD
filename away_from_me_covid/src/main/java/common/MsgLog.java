package common;

public enum MsgLog {
    AccountAlreadyExists(0),
    AccountCreated(1),
    InvalidCounty(2),
    UserAlreadyExists(3),
    HealthNumberAlreadyExists(4),
    InvalidLogin(5),
    SucceedLogin(6),
    DefinedAsInfected(7),
    DefineContact(8),
    MakeTest(9),
    Logout(10),
    YouHaveBeenInContactWithInfectedPerson(11),
    SignUp(12),
    SignIn(13),
    InvalidPassword(14),
    InvalidHealthNumber(15);

    public int log;

    MsgLog(int logNumber) {
        this.log = logNumber;
    }
}