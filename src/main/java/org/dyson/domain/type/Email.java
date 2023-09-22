package org.dyson.domain.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dyson.core.model.ValueObject;
import org.hibernate.validator.internal.util.DomainNameUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // Danh cho hibernate
public class Email implements ValueObject {
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final String LOCAL_PART_ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uFFFF-]";
    private static final String LOCAL_PART_INSIDE_QUOTES_ATOM = "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uFFFF-]|\\\\\\\\|\\\\\\\")";
    /**
     * Regular expression for the local part of an email address (everything before '@')
     */
    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile(
        "(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" +
            "(?:\\." + "(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" + ")*", CASE_INSENSITIVE
    );

    private final String value;

    public Email(String email) {
        this.value = email;
    }

    public static Email valueOf(String email) {
        return new Email(email);
    }

    public static Email valueOfNullable(String email) {
        if (email == null) {
            return null;
        }
        return Email.valueOf(email);
    }

    public boolean isValid(CharSequence value) {
        if (value == null || value.length() == 0) {
            return true;
        }

        // cannot split email string at @ as it can be a part of quoted local part of email.
        // so we need to split at a position of last @ present in the string:
        String stringValue = value.toString();
        int splitPosition = stringValue.lastIndexOf('@');

        // need to check if
        if (splitPosition < 0) {
            return false;
        }

        String localPart = stringValue.substring(0, splitPosition);
        String domainPart = stringValue.substring(splitPosition + 1);

        if (!isValidEmailLocalPart(localPart)) {
            return false;
        }

        return DomainNameUtil.isValidEmailDomainAddress(domainPart);
    }

    private boolean isValidEmailLocalPart(String localPart) {
        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            return false;
        }
        Matcher matcher = LOCAL_PART_PATTERN.matcher(localPart);
        return matcher.matches();
    }
}
