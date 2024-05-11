public class ReservationClass {
    private String userId;
    private String date;
    private int numberOfPeople;

    public ReservationClass() {
        // Üres konstruktor szükséges a Firebase adatbázishoz
    }

    public ReservationClass(String userId, String date, int numberOfPeople) {
        this.userId = userId;
        this.date = date;
        this.numberOfPeople = numberOfPeople;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
}

