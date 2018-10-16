package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_HOMEPAGE_IO_EXCEPTION;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Class for Registration Officer details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class RegistrationOfficerDetailsController extends BaseController {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@FXML
	private Label registrationOfficerName;

	@FXML
	private Label registrationOfficeId;

	@FXML
	private Label registrationOfficeLocation;

	/**
	 * Mapping Registration Officer details
	 */
	public void initialize() {

		LOGGER.debug("REGISTRATION - OFFICER_DETAILS - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Displaying Registration Officer details");

		SessionContext sessionContext = SessionContext.getInstance();
		registrationOfficerName.setText(sessionContext.getUserContext().getName());
		registrationOfficeId
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
		registrationOfficeLocation
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
	}

	/**
	 * Redirecting to Home page on Logout and destroying Session context
	 */
	public void logout(ActionEvent event) {
		try {
			String initialMode = SessionContext.getInstance().getMapObject()
					.get(RegistrationUIConstants.LOGIN_INITIAL_SCREEN).toString();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Clearing Session context");

			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Restoring Login sequence after Logout");

			String fxmlPath = null;
			switch (initialMode) {
			case RegistrationUIConstants.OTP:
				fxmlPath = RegistrationUIConstants.LOGIN_OTP_PAGE;
				break;
			case RegistrationUIConstants.LOGIN_METHOD_PWORD:
				fxmlPath = RegistrationUIConstants.LOGIN_PWORD_PAGE;
				break;
			default:
				fxmlPath = RegistrationUIConstants.LOGIN_PWORD_PAGE;
				break;
			}

			BorderPane loginpage = BaseController.load(getClass().getResource(RegistrationUIConstants.INITIAL_PAGE));
			AnchorPane loginType = BaseController.load(getClass().getResource(fxmlPath));
			loginpage.setCenter(loginType);
			RegistrationAppInitialization.getScene().setRoot(loginpage);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Logout ", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), ioException.getMessage());
		}
	}

	/**
	 * Redirecting to Home page
	 */
	public void redirectHome(ActionEvent event) {
		try {

			LOGGER.debug("REGISTRATION - REDIRECT_HOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Redirecting to Home page");

			VBox homePage = BaseController.load(getClass().getResource(RegistrationUIConstants.HOME_PAGE));
			RegistrationAppInitialization.getScene().setRoot(homePage);

		} catch (IOException | RuntimeException exception) {
			generateAlert(RegistrationUIConstants.ALERT_ERROR, AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
					REG_UI_HOMEPAGE_IO_EXCEPTION.getErrorMessage());
		}
	}
}
