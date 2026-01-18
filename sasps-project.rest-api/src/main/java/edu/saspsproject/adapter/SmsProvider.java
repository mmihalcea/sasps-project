package edu.saspsproject.adapter;

/**
 * ADAPTER PATTERN - SMS Provider Interface
 * 
 * Acest pattern permite integrarea uniformă a diferiților provideri de SMS
 * (Orange, Vodafone, Telekom) printr-o interfață comună.
 * 
 * Beneficii:
 * - Abstractizează complexitatea API-urilor diferite ale operatorilor
 * - Permite schimbarea providerului fără impact asupra codului client
 * - Facilitează implementarea failover între provideri
 * - Respectă Dependency Inversion Principle (DIP)
 * 
 * @see OrangeSmsAdapter
 * @see VodafoneSmsAdapter
 * @see TelekomSmsAdapter
 */
public interface SmsProvider {
    
    /**
     * Trimite un SMS către numărul de telefon specificat.
     * 
     * @param phoneNumber numărul de telefon în format național (07xxxxxxxx)
     * @param message mesajul SMS (maxim 160 caractere pentru un SMS standard)
     * @return true dacă SMS-ul a fost trimis cu succes
     */
    boolean sendSms(String phoneNumber, String message);
    
    /**
     * Verifică dacă providerul suportă numărul de telefon dat.
     * 
     * @param phoneNumber numărul de telefon de verificat
     * @return true dacă providerul poate trimite SMS la acest număr
     */
    boolean supports(String phoneNumber);
    
    /**
     * Returnează numele providerului.
     * 
     * @return numele providerului SMS
     */
    String getProviderName();
}
