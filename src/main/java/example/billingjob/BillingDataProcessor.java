package example.billingjob;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

/*
Para calcular el gasto mensual, necesitamos el precio del uso de datos, llamadas y mensajes de texto. En este laboratorio,
y para simplificar las cosas, suponemos que el precio de los datos es el siguiente:
    Precio de las llamadas: $0,50 por minuto
    Precio de los mensajes de texto/SMS: $0,10 cada uno
    Precio de los datos: $0,01 por MB
*/



public class BillingDataProcessor implements ItemProcessor<BillingData, ReportingData> {

    private final PricingService pricingService;

    public BillingDataProcessor(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @Value("${spring.cellular.spending.threshold:150}")
    private float spendingThreshold;

    @Override
    public ReportingData process(BillingData item) {
        double billingTotal =
                item.dataUsage() * pricingService.getDataPricing() +
                        item.callDuration() * pricingService.getCallPricing() +
                        item.smsCount() * pricingService.getSmsPricing();
        if (billingTotal < spendingThreshold) {
            return null;
        }
        return new ReportingData(item, billingTotal);
    }
}