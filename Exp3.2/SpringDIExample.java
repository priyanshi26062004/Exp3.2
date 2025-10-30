package com.spring.di;

import org.springframework.context.annotation.*;

// Service interface
interface MessageService {
    void sendMessage(String message);
}

// Implementation class
class EmailService implements MessageService {
    @Override
    public void sendMessage(String message) {
        System.out.println("📧 Email Sent: " + message);
    }
}

// Dependent class
class UserController {
    private final MessageService messageService;

    // Constructor-based DI
    public UserController(MessageService messageService) {
        this.messageService = messageService;
    }

    public void processMessage(String msg) {
        messageService.sendMessage(msg);
    }
}

// Spring Java-based configuration
@Configuration
class AppConfig {
    @Bean
    public MessageService messageService() {
        return new EmailService();
    }

    @Bean
    public UserController userController() {
        return new UserController(messageService());
    }
}

// Main class
public class SpringDIExample {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        UserController controller = context.getBean(UserController.class);
        controller.processMessage("Welcome to Spring Dependency Injection!");
        context.close();
    }
}
