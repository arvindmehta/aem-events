# Event Registration Web Project

## Project Overview
This project is an Event Registration web page created using Adobe Experience Manager (AEM) and the latest project archetype. The page leverages various out-of-the-box components and custom services to handle form submissions.

## Project Structure

The key components and services used in the project are described below.

## Components Used

* **Title**:Displays the title of the event.
* **Text**:Provides descriptive text about the event.
* **Image**: Adds images related to the event.
* **Form Container**: Customization: This component was overridden to allow custom form actions. The **FormContainerModel**.java and **container**.html were overridden to submit the form to a custom servlet.
* **Form Text**: Text fields for user input.
* **Form Button**:Button to submit the form.
* **Experience Fragment** for Header and Footer: Reusable components for the page header and footer.

## Form Submission Workflow
The form submission process is handled by a custom servlet and service classes as outlined below:

## EventRegistrationServlet

* Resource Type: **EventRegistrationServlet**
* Purpose: Handles form submissions by invoking the registerEvent method of the eventRegistrationService.
*  Post-submission: Saves the user response in a JCR node upon successful response from the mock web service. The save location in JCR is managed through a configurable Hidden Form component.

## EventRegistrationService

* Service: **EventRegistrationService**
* Service Implementation: **EventRegistrationServiceImpl**
* Method: registerEvent
* Function: Calls the postResponse method of the **RestAPIService** to interact with a mock web service.

## RestAPIService

* Service: **RestAPIService**
* Service Implementation: **RestAPIServiceImpl**
* Purpose: Responsible for interacting with the mock web service to post data and return a mock success response.
* Configuration: Managed under ui.config, specifically handling the HOST API URL

## RestAPIService

* Class: **RestClientUtil**
* Purpose: Handles HTTP client connections, connection timeouts, and socket timeout exceptions. It also manages adding basic request headers for the HTTP requests.
* Reusability: Designed to be reusable across different services requiring HTTP connections.

## User Mapper Configuration
* Purpose: Provides the resource resolver via a System user, enabling the API response to be saved in the JCR.
* Configuration: Managed under ui.config

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

## Access the Web Page:
* Event registration page(/content/aem-events/us/en/event-registration) with Text, Image, Form Container and Form Text Fields
* Thankyou Page (/content/aem-events/us/en/event-registration/thank-you)

## Softwares Used
* AEM version: aem-sdk-2024.06.16647.20240607T103723Z-240500
* Java SDK: JDK 11
* Maven: 3.8.4
