# Ticket Booking Service

## **Overview**
A Java 11+ Ticket Booking Service that enforces business rules for ticket purchases, including:
- Three ticket types: **Adult (£25), Child (£15), and Infant (£0)**.
- Infants do **not** get a seat and must accompany an adult.
- **Max 25 tickets** per purchase, requiring at least **one Adult ticket**.
- Integrates with external services for **payments** and **seat reservations**.

## **Tech Stack**
- **Java 11+**
- **Maven** (Build Tool)
- **JUnit 5 & Mockito** (Testing)
- **SOLID Principles & Design Patterns**

## **Installation & Setup**
### **Prerequisites**
- Install **Java 11+** and **Maven**.
- Use an IDE (**IntelliJ IDEA**, **Eclipse**, or **VS Code**).

### **Run the Application**
1. **Clone the Repository**  
   ```sh
   git clone https://github.com/your-repo/ticket-booking-service.git
   cd ticket-booking-service
   Build the project : mvn clean install
   run test : mvn test

Design Patterns Used
Factory Pattern → Ticket creation.
Strategy Pattern → Ticket pricing logic.
Singleton Pattern → Ensures a single service instance.

Future Enhancements
Implement Spring Boot REST API.
Add logging and improved exception handling.
Integrate with a real payment system.
 
