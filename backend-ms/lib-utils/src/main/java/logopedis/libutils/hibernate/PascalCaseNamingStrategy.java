package logopedis.libutils.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;


// в бд используется стиль PascalCase
// а в hibernate snake_case

public class PascalCaseNamingStrategy implements PhysicalNamingStrategy {

    // использовать имя как задано в entity и экранировать его
    private Identifier quote(Identifier name) {
        if (name == null) return null;
        return Identifier.toIdentifier(name.getText(), true); // <-- quoted = true
    }

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return quote(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return quote(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return quote(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return quote(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return quote(name);
    }
}

