<<<<<<< HEAD
﻿# VIS_Application - Vehicle Information System

## Project Structure
This is a JavaFX-based Vehicle Information System application.

## Prerequisites
- Java 17 or higher
- JavaFX SDK 25.0.2 (located at: C:\Users\Pabal\Downloads\openjfx-25.0.2_windows-x64_bin-sdk\javafx-sdk-25.0.2)
- Maven
- PostgreSQL Database

## Setup Instructions
1. Import as Maven project in IntelliJ IDEA
2. Configure JavaFX SDK in project structure
3. Set up PostgreSQL database
4. Update database connection settings in DatabaseConnection.java
5. Build and run the application

## Features
- Multi-role authentication (Admin, Police, Customer, Workshop)
- Vehicle registration and tracking
- Violation management
- Service records
- Insurance management
- PDF export and QR code generation
- Email notifications
- Gamification system
- Backup service

## Technologies
- JavaFX for UI
- PostgreSQL for database
- Maven for dependency management
- iText7 for PDF generation
- ZXing for QR codes
- JavaMail for email services
=======
# VIS_Application
VIS Application is a JavaFX desktop system for vehicle management, traffic violation tracking, and service records in Lesotho. Features role-based dashboards for Admin, Police, Customer, and Workshop users with PostgreSQL backend, PDF/CSV reports, and email notifications.
>>>>>>> 60a0c183562afea58828859525ae42665fbb6a82

# VIS Application - Vehicle Information System

A comprehensive JavaFX desktop application for managing vehicles, traffic violations, service records, and police reports in Lesotho.

## Features
- 👑 **Admin Dashboard** - User management, analytics, reports
- 👮 **Police Dashboard** - Violation recording, vehicle search, reports
- 👤 **Customer Dashboard** - Vehicle registration, violation payment, service scheduling
- 🔧 **Workshop Dashboard** - Service management, invoicing

## Tech Stack
- Java 17, JavaFX 25.0.2
- PostgreSQL 14+
- Maven
- iText7 (PDF), ZXing (QR Codes)

## Quick Start
```bash
git clone https://github.com/paballophuthi/VIS_Application.git
cd VIS_Application
mvn clean javafx:run
