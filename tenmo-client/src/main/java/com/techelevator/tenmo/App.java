package com.techelevator.tenmo;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
	public static String AUTH_TOKEN = "";
	public static String USERNAME = "";
	public static int ID = 0;
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    public AuthenticationService authenticationService;

    public AccountService accountService = new AccountService(API_BASE_URL, currentUser);
	public UserService userService = new UserService(API_BASE_URL, currentUser);
	public TransferService transferService = new TransferService(API_BASE_URL, currentUser);
	private final RestTemplate restTemplate = new RestTemplate();
	
    public static void main(String[] args) {
    	
    	// code that was here \/\/
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {

		BigDecimal balance = userService.getBalanceByExchange();
		System.out.println("Your current account balance is: $" + balance);
		
	}

	private void viewTransferHistory() {
		Transfer[] transfers = null;
		int accountId = 0;
		accountId = restTemplate.exchange(API_BASE_URL + "accounts/" + App.ID, HttpMethod.GET,
				makeAuthEntity(), int.class).getBody();
	
		transfers = transferService.getTransfersByAccountId(accountId);
		console.printTransfers(transfers, accountId);
		
		int transferID = -1;
		
		try {
		transferID = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
		console.printTransferDetailByID(transferID, transfers);
		} catch (Exception e){
			System.out.println("Invalid transfer ID!");
		}
	}

	private boolean viewPendingRequests() {
		
		Transfer[] transfers = null;
		transfers = transferService.listPendingTransfersByAccountId(App.ID);
		
		int accountId = 0;
		// gets account id by senderId
		accountId = restTemplate.exchange(API_BASE_URL + "accounts/" + App.ID, HttpMethod.GET,
				makeAuthEntity(), int.class).getBody();
		
		console.printTransfers(transfers, accountId);
		
		int transferId = 0;
		
		try {
			
		
		transferId = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");
		
		// if account_to = users account then it's the same account that made it, can't "approve own request" return false
		
		for (int i=0; i<transfers.length; i++) {
			if (transfers[i].getAccount_to() == accountId) {
				System.out.println("\nCan't approve or reject own request");
				return false;
			}
		}
		
		console.printApproveorDeny(); 
		
		int approveOrDeny = -1;
		Transfer selectedTransfer = new Transfer();
		
		while (approveOrDeny < 0 || approveOrDeny > 2) {
			approveOrDeny = console.getUserInputInteger("Please choose an option");
		}
		if (approveOrDeny == 1) {
			
			for (int i=0; i<transfers.length; i++) {
				if (transfers[i].getTransfer_id() == transferId) {
					selectedTransfer = transfers[i];
				}
			}
			// check money and deduct/increase if good
			String username = "";
			username = restTemplate.exchange(API_BASE_URL + "users/account/" + selectedTransfer.getAccount_to(), 
					HttpMethod.GET, makeAuthEntity(), String.class).getBody();
			
			boolean enoughMoney = false;
			//below method used to be called sendMoney
			enoughMoney = userService.validateTransfer(App.ID, selectedTransfer.getAmount(), username, selectedTransfer.getAccount_to());
			
			int requestedUserId = 0;
			requestedUserId = restTemplate.exchange(API_BASE_URL + "users/userId/" + selectedTransfer.getAccount_to(), 
					HttpMethod.GET, makeAuthEntity(), int.class).getBody();
					
			if (enoughMoney) {
				accountService.increaseBalance(requestedUserId, selectedTransfer.getAmount());
				accountService.decreaseBalance(App.ID, selectedTransfer.getAmount());
				System.out.println("\nTransfer accepted!");
				
				transferService.changeTransferStatus(transferId, 2);
				
				return enoughMoney;
			}
			
			
		} else if (approveOrDeny == 2) {
			
			 transferService.changeTransferStatus(transferId, 3);
			System.out.println("\nTransfer rejected!");
			return true;
			
		} else if (approveOrDeny == 0) {
			return false;
		} 
		
		} catch (Exception e) {
			System.out.println("\nInvalid selection!");
		}
		return false;
	}

	private boolean sendBucks() {
		
		// method iterates thru array and prints users that aren't you
		console.printUsers(userService.listAllUsers(currentUser.getUser().getId()));  
		
		Integer idSendingTo = -1;
		idSendingTo = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
		if (idSendingTo == 0 ) {
			return false;
		}
		String amount = "";
		amount = console.getUserInput("Enter amount");
		
		BigDecimal amountBD = BigDecimal.valueOf(Double.parseDouble(amount));
		
		boolean success = false;
		//below method used to be called sendMoney
		success = userService.validateTransfer(idSendingTo, amountBD, currentUser.getUser().getUsername(), currentUser.getUser().getId());
		if (success) {
			
			transferService.createSendTransfer(App.ID, idSendingTo, amountBD);
			accountService.decreaseBalance(App.ID, amountBD);
			accountService.increaseBalance(idSendingTo, amountBD);
		}
		return true;
	}

	private boolean requestBucks() {
		//list users not you
		try {
		console.printUsers(userService.listAllUsers(currentUser.getUser().getId()));  
		
		Integer idSendingTo = -1;
		idSendingTo = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
		if (idSendingTo == 0 ) {
			return false;
		}
		String amount = "";
		amount = console.getUserInput("Enter amount");
		
		BigDecimal amountBD = BigDecimal.valueOf(Double.parseDouble(amount));
		
		transferService.createRequestTransfer(idSendingTo, currentUser.getUser().getId(), amountBD);
		} catch (Exception e) {
			System.out.println("\nInvalid Selection!");
		}
		return true;
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				
				// we added this
				App.AUTH_TOKEN = currentUser.getToken();
				App.USERNAME = currentUser.getUser().getUsername();
				App.ID = currentUser.getUser().getId();
				
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	
	public HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}
}
