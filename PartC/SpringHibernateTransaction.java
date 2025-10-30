package com.springhib.txn;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import jakarta.persistence.*;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

// Entity class
@Entity
@Table(name = "account")
class Account {
    @Id
    private int accNo;
    private String holderName;
    private double balance;

    public Account() {}
    public Account(int accNo, String holderName, double balance) {
        this.accNo = accNo;
        this.holderName = holderName;
        this.balance = balance;
    }

    public int getAccNo() { return accNo; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String toString() {
        return accNo + " | " + holderName + " | Balance: " + balance;
    }
}

// Service Interface
interface BankService {
    void transfer(int fromAcc, int toAcc, double amount);
}

// Implementation with Transaction Management
class BankServiceImpl implements BankService {
    private final SessionFactory factory;

    public BankServiceImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    @Transactional
    public void transfer(int fromAcc, int toAcc, double amount) {
        Session session = factory.getCurrentSession();
        Account from = session.get(Account.class, fromAcc);
        Account to = session.get(Account.class, toAcc);

        if (from.getBalance() < amount) {
            throw new RuntimeException("❌ Insufficient balance!");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        session.update(from);
        session.update(to);

        System.out.println("💸 Transferred ₹" + amount + " from Acc#" + fromAcc + " to Acc#" + toAcc);
    }
}

// Spring Configuration
@Configuration
@EnableTransactionManagement
class AppConfig {
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/bankdb");
        ds.setUsername("root");
        ds.setPassword("yourpassword");
        return ds;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Account.class).buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory factory) {
        return new HibernateTransactionManager(factory);
    }

    @Bean
    public BankService bankService(SessionFactory factory) {
        return new BankServiceImpl(factory);
    }
}

// Main Class
public class SpringHibernateTransaction {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(AppConfig.class);

        SessionFactory factory = ctx.getBean(SessionFactory.class);
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        // Initialize accounts
        session.save(new Account(1, "Priya", 5000));
        session.save(new Account(2, "Amit", 3000));
        tx.commit();

        // Perform transaction
        Transaction txn = factory.openSession().beginTransaction();
        BankService service = ctx.getBean(BankService.class);
        service.transfer(1, 2, 1500);
        txn.commit();

        // Display balances
        Session s = factory.openSession();
        System.out.println("\n✅ Final Balances:");
        System.out.println(s.get(Account.class, 1));
        System.out.println(s.get(Account.class, 2));

        ctx.close();
    }
}
