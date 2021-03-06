package org.dreamexposure.tap.backend.utils;

import org.dreamexposure.novautils.crypto.KeyGenerator;
import org.dreamexposure.tap.backend.network.database.ConfirmationDataHandler;
import org.dreamexposure.tap.core.conf.GlobalVars;
import org.dreamexposure.tap.core.objects.account.Account;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class Generator {
    public static String generateEmailConfirmationLink(Account account) {
        String code = KeyGenerator.csRandomAlphaNumericString(32);
        
        //Save to database
        ConfirmationDataHandler.get().addPendingConfirmation(account, code);
    
        return GlobalVars.apiUrl + "/confirm/email?code=" + code;
    }
}
