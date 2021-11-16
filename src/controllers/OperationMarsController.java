package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import models.Coordinate;
import models.Move;
import models.Plateau;
import models.Rover;
import models.State;
import utils.CompassDirections;

public class OperationMarsController {
	public Integer nameSequence = 0;

	public void processCommand(String line, List<Rover> roverList) {
		String roverDeployedDirection = "";
		boolean isRoverDeployed = false;
		Rover selectedRover = null;
		Coordinate roverInitialCoordinate = null;

		if (!roverList.isEmpty()) {
			selectedRover = roverList.get(roverList.size() - 1);
		}
		List<String> letters = Arrays.asList(line.split(" "));
		for (int a = 0; a < letters.size(); a++) {
			Pattern digitPattern = Pattern.compile(".*[0-9].*");
			if (digitPattern.matcher(letters.get(a)).matches()) {
				roverInitialCoordinate = prepareNewRover(selectedRover, roverInitialCoordinate, letters.get(a));
			} else {
				deployOrMoveRover(roverInitialCoordinate, isRoverDeployed, roverDeployedDirection, nameSequence,
						roverList, selectedRover, letters.get(a));
			}
		}
	}

	public String printRoversLastStates(List<Rover> roverList) {
		StringBuilder sb = new StringBuilder();
		if (roverList != null) {
			for (Rover rover : roverList) {
				sb.append(rover.getName());
				sb.append(" ");
				sb.append(rover.getState().getCoordinates().getHorizontal().toString());
				sb.append(" ");
				sb.append(rover.getState().getCoordinates().getVertical().toString());
				sb.append(" ");
				sb.append(rover.getState().getDirection().getShortCode());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private void deployOrMoveRover(Coordinate roverInitialCoordinate, boolean isRoverDeployed,
			String roverDeployedDirection, Integer nameSequence, List<Rover> roverList, Rover selectedRover,
			String letter) {
		if (roverInitialCoordinate != null && roverInitialCoordinate.getHorizontal() != null
				&& roverInitialCoordinate.getVertical() != null && !isRoverDeployed) {
			roverDeployedDirection = letter;
			isRoverDeployed = true;
			this.nameSequence = this.nameSequence + 1;

			roverList.add(createRover(this.nameSequence, roverInitialCoordinate,
					CompassDirections.getCompassDirectionFromCode(roverDeployedDirection)));
		} else if (selectedRover != null) {
			turnOrMove(selectedRover, letter);
		}
	}

	private void turnOrMove(Rover selectedRover, String letter) {
		Move move = new Move();
		char[] ch = letter.toCharArray();
		for (int b = 0; b < ch.length; b++) {
			if ('L' == ch[b]) {
				selectedRover.setState(move.turnLeft(selectedRover.getState()));
			} else if ('R' == ch[b]) {
				selectedRover.setState(move.turnRight(selectedRover.getState()));
			} else if ('M' == ch[b]) {
				selectedRover.setState(move.moveForward(selectedRover.getState()));
			}
		}
	}

	private Coordinate prepareNewRover(Rover selectedRover, Coordinate roverInitialCoordinate, String letter) {
		selectedRover = null;
		if (roverInitialCoordinate == null) {
			roverInitialCoordinate = new Coordinate(null, null);
		}
		setCoordinates(roverInitialCoordinate, letter);
		return roverInitialCoordinate;
	}

	private Rover createRover(Integer nameSequence, Coordinate roverInitialCoordinate,
			CompassDirections compassDirections) {
		State state = new State(
				new Coordinate(roverInitialCoordinate.getHorizontal(), roverInitialCoordinate.getVertical()),
				compassDirections);
		Rover rover = new Rover(state, findRoverName(nameSequence));
		roverInitialCoordinate = null;
		return rover;
	}

	private void setCoordinates(Coordinate coordinate, String letter) {
		if (coordinate.getHorizontal() == null) {
			coordinate.setHorizontal(Integer.valueOf(letter));
		} else {
			coordinate.setVertical(Integer.valueOf(letter));
		}
	}

	private String findRoverName(Integer nameSequence) {
		StringBuilder sb = new StringBuilder();
		sb.append("Rover");
		sb.append(nameSequence.toString());
		return sb.toString();
	}

	public Plateau createPlateau(Integer maxHorizontalCoordinates, Integer maxVerticalCoordinates) {
		return new Plateau(maxHorizontalCoordinates, maxVerticalCoordinates);
	}

	public String processCommands() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter plateu horizontal border:");
		Integer maxHorizontalCoordinates = Integer.valueOf(scanner.nextLine());
		System.out.println("Enter vertical border:");
		Integer maxVerticalCoordinates = Integer.valueOf(scanner.nextLine());
		Plateau plateau = createPlateau(maxHorizontalCoordinates, maxVerticalCoordinates);
		//plateau object has been created for if more validation will be needed.
		List<Rover> roverList = new ArrayList<Rover>();

		System.out.println("Enter your commmand: (Write Exit to finish explore plateu)");

		while (scanner.hasNextLine()) {
			System.out.println("Enter your commmand: (Write Exit to finish explore plateu)");
			String command = scanner.nextLine();
			if (command.equals("Exit")) {
				System.out.println("Last rovers coordinates:");
				String output=printRoversLastStates(roverList);
				System.out.println(output);				
				System.exit(0);
				scanner.close();
			} else {
				processCommand(command, roverList);
			}
		}
		return printRoversLastStates(roverList);
	}
	
}
