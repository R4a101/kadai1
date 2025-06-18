package model;

public class Hospital {
    private String hospitalId;
    private String hospitalName;
    private String address;
    private String phone;
    private String email;
    private int capital; // 資本金フィールドを追加

    // コンストラクタにemailとcapitalを追加
    public Hospital(String hospitalId, String hospitalName, String address, String phone, String email, int capital) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.address = address;
        this.phone = phone;
        this.email = email; // emailの代入が抜けていたのを修正
        this.capital = capital;
    }

    // 元のコンストラクタも残す場合（オーバーロード）
    public Hospital(String hospitalId, String hospitalName, String address, String phone, String email) {
        this(hospitalId, hospitalName, address, phone, email, 0); // capitalのデフォルト値を0とするなど
    }
    
    // 元の引数が4つのコンストラクタも修正 (emailがnullになる可能性があった)
    public Hospital(String hospitalId, String hospitalName, String address, String phone) {
        this(hospitalId, hospitalName, address, phone, null, 0); 
    }


    public String getHospitalId() { return hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getCapital() { return capital; } // capitalのgetter

    // 必要に応じてsetterも追加
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setCapital(int capital) { this.capital = capital; }
}