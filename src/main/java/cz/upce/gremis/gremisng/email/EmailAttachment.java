package cz.upce.gremis.gremisng.email;

import jakarta.mail.util.ByteArrayDataSource;

public record EmailAttachment(String name, ByteArrayDataSource content) {
}
