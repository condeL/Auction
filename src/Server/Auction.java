package Server;

import java.util.Date;

public class Auction {

    private String name;
    private float starting_price;
    private float reserve_price;
    private float current_bid;
    private String seller_name;
    private boolean active;
    private boolean canceled;
    private String seller_phone;
    private String bidder_name;
    private String bidder_phone;
    Date time;

    Auction(String name, float starting, float reserve, String seller_name, String seller_phone, Date date){
        this.name = name;
        this.starting_price = starting;
        this.reserve_price = reserve;
        this.current_bid = starting;
        this.seller_name = seller_name;
        this.seller_phone = seller_phone;
        this.time = date;
        active = true;

    }



    synchronized boolean updateBid(float bid, String bidder_name, String bidder_phone){
        if (bid > current_bid){
            current_bid = bid;
            this.bidder_name = bidder_name;
            this.bidder_phone = bidder_phone;
            return true;
        }
        else
            return false;

    }

    public boolean checkReserve(){
        return current_bid >= reserve_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStarting_price() {
        return starting_price;
    }

    public float getReserve_price() {
        return reserve_price;
    }

    public float getCurrent_bid() {
        return current_bid;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getSeller_phone() {
        return seller_phone;
    }

    public String getBidder_name() {
        return bidder_name;
    }

    public void setBidder_name(String bidder_name) {
        this.bidder_name = bidder_name;
    }

    public String getBidder_phone() {
        return bidder_phone;
    }

    public void setBidder_phone(String bidder_phone) {
        this.bidder_phone = bidder_phone;
    }

    public boolean isActive() {
        return active;
    }

    public synchronized void stopAuction() {
        active = false;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public synchronized void setCanceled() {
        canceled = true;
    }
}
