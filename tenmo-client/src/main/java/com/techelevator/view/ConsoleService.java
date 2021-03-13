package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class ConsoleService {
	
	private static final String API_BASE_URL = "http://localhost:8080/";
	private final RestTemplate restTemplate = new RestTemplate();
	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}
	
	public void printUsers(List<User> users) {
        if (users != null) {
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID          Name");
            System.out.println("-------------------------------------------");
            for (User user : users) {
                System.out.println(user.getId() + "        " + user.getUsername());
            }
            System.out.println("---------\n");
        }
    }
	
	public void printTransfers(Transfer[] transfers, int accountId) {
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.println("\nID          From/To             Amount");
		System.out.println("-------------------------------------------");
		
		String currentTo = "";
		String currentFrom = "";
		
		for (Transfer tran : transfers) {
			System.out.print(tran.getTransfer_id() + "        ");
					if (tran.getAccount_from() == accountId) {
						
						currentTo =	restTemplate.exchange(API_BASE_URL + "users/account/" + tran.getAccount_to(),
						HttpMethod.GET, makeAuthEntity(),  String.class).getBody();
						System.out.print("To: " + currentTo);
						
					} else {
						currentFrom = restTemplate.exchange(API_BASE_URL + "users/account/" + tran.getAccount_from(),
						HttpMethod.GET, makeAuthEntity(),  String.class).getBody();
						System.out.print("From: " + currentFrom);
					} 
					// added tabs to help format
					System.out.println("    \t$" + tran.getAmount());
		}
		System.out.println("---------");
	}
	
	public void printApproveorDeny() {
		System.out.println("1: Approve");
		System.out.println("2: Reject");
		System.out.println("0: Don't approve or reject");
		System.out.println("---------");
	}
	
	public boolean printTransferDetailByID(int transferId, Transfer[] transfer) {
		Transfer result = null;
		boolean success = false;
		
		if (transferId == 0) {
			return success;
		}
		
		for(int i = 0; i < transfer.length; i++) {
			if (transfer[i].getTransfer_id() == transferId) {
				result = transfer[i];
				success = true;
			}
		}
		
		if (result == null) {
			System.out.println("\nInvalid Transfer ID");
			return success;
		}
		
		String transferType = "";
		if (result.getTransfer_type_id() == 1) {
			transferType = "Request";
		} else {
			transferType = "Send";
		}
		
		String transferStatus = "";
		
		if (result.getTransfer_status_id() == 1) {
			transferStatus = "Pending";
		} else if (result.getTransfer_status_id() == 2) {
			transferStatus = "Approved";
		} else {
			transferStatus = "Rejected";
		}
		
		System.out.println("--------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------------------------------");
		System.out.println("Id: " + result.getTransfer_id());
		System.out.println("From: " + restTemplate.exchange(API_BASE_URL + "users/account/" + result.getAccount_from(),
								HttpMethod.GET, makeAuthEntity(),  String.class).getBody());
		System.out.println("To: " +  restTemplate.exchange(API_BASE_URL + "users/account/" + result.getAccount_to(),
				HttpMethod.GET, makeAuthEntity(),  String.class).getBody());
		System.out.println("Type: " + transferType);
		System.out.println("Status: " + transferStatus);
		System.out.println("Amount: $" + result.getAmount());
		
		return success;
	}
	
	public HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}
	
}
