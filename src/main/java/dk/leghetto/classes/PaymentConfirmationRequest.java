package dk.leghetto.classes;

public class PaymentConfirmationRequest {

    private String paymentLinkId;
    private String paymentIntentId;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

}
