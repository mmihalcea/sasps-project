package edu.saspsproject.adapter;

/**
 * ADAPTER PATTERN - Email Provider Interface
 * 
 * Acest pattern permite integrarea uniformă a diferiților provideri de email
 * (Gmail, Government Email, Local Government Email) printr-o interfață comună.
 * 
 * Beneficii:
 * - Decuplează codul de implementările specifice ale providerilor
 * - Permite adăugarea de noi provideri fără modificarea codului existent
 * - Respectă Open/Closed Principle (OCP)
 * - Facilitează testarea prin mock-uri
 * 
 * @see GovEmailAdapter
 * @see LocalGovEmailAdapter
 * @see GenericEmailAdapter
 */
public interface EmailProvider {
    
    /**
     * Trimite un email către destinatar.
     * 
     * @param to adresa email a destinatarului
     * @param subject subiectul email-ului
     * @param content conținutul email-ului (HTML sau text)
     * @return true dacă email-ul a fost trimis cu succes
     */
    boolean sendEmail(String to, String subject, String content);
    
    /**
     * Returnează tipul de provider.
     * Folosit pentru logging și debugging.
     * 
     * @return numele providerului
     */
    String getProviderName();
}
