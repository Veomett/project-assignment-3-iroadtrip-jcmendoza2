// Cade Mendoza 12/6/2023
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IRoadTrip {
	/* member class variables: hashmap that will hold the country's that share 
	 * that share borders with main country as you read from files
	 * countryABB is mainly from capdist and state_name files since borders.txt doesn't have it
	 * but used to be able to reference both 
	 * distances between capitals since we want capital distance and not border distance.
	 */
    private Map<String, List<String>> countryBorders; 
    private Map<String, String> countryAbb; 
    private Map<String, Integer> distances_wCapitals; 

    public IRoadTrip(String[] args) {
		// initalize the hashmaps in the constructor IRoadTrip
        countryBorders = new HashMap<>();
        countryAbb = new HashMap<>();
        distances_wCapitals = new HashMap<>();
		// open the provided files in their functions 
		readBorders(args[0]);
        readCapitalDist(args[1]);
        readStateName(args[2]);
	}
	
	private void readBorders(String fileName) {
		// try to read the file, if file doesn't open catch the error and print the error 
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
			// while we haven't reached the end of the file, split each line at the "=" char 
			// separating main country and its neighborBorders 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" = ");
				// get the main country without the (name) if there's an alias 
                String countryName = parts[0].trim().split("\\(")[0].trim(); 
				// split the number of neighborBorders at the ";" char saving new str
                String[] borders = parts.length > 1 ? parts[1].split(";") : new String[0];

                List<String> neighborBorders = new ArrayList<>();
				// for each border shared, we will save the name without the () and add it to the ArrayList
                for (String border : borders) {
                    String neighbor = border.split("\\d")[0].trim().split("\\(")[0].trim(); 
                    neighborBorders.add(neighbor);
                }
				// place the neighborBorders with the country they share the border to in the hashmap 
                countryBorders.put(countryName, neighborBorders);
            }
			// close the file to prevent overflow or IO problems 
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
			System.exit(1);
        }
	}

    private void readCapitalDist(String fileName) {
		// try to read the file, if file doesn't open catch the error and exit 
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
			// while the file isn't null skip the first line in the file since that's
			// the description line 
            while ((line = br.readLine()) != null) {
                if (line.startsWith("numa")) {
                    continue;
                }
				// split at the comma, save each country's Abbreviation 
                String[] parts = line.split(",");
                String countryA = parts[1].trim();
                String countryB = parts[3].trim();
				// and save the distance in km 
                int distance = Integer.parseInt(parts[4].trim());
				// add the distances of the capitals both ways in the hashmap so we can interchange
				// directions when inputting user information 
                distances_wCapitals.put(countryA + "_" + countryB, distance);
                distances_wCapitals.put(countryB + "_" + countryA, distance);
            }
			//clsoe the file 
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
			System.exit(1);
        }
	}

    private void readStateName(String fileName) {
		// try to read the file and if file doesn't open then print error and exit 
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
			// while the file isn't null 
            while ((line = br.readLine()) != null) {
				// since it's a Excel file or csv file, data is separated by tabs so split at tabs 
                String[] parts = line.split("\t");
				// we only want countries that are most recent which is why we check 2020 etc and make sure
				// all data exists, id, stateid, name, date 
                if (parts.length >= 5 && parts[4].equals("2020-12-31")) { 
                    String countryId = parts[1].trim();
                    String countryName = parts[2].trim().split("\\(")[0].trim(); 
                    countryAbb.put(countryName, countryId);
					// add the country ID to countryabb
                }
            }
			// close the file 
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDistance(String country1, String country2) {
		// if the countries aren't in our countryBorders hashmap then we return -1
        if (!countryBorders.containsKey(country1) || !countryBorders.containsKey(country2)) {
            return -1; 
        }
		// we have to check now if the abbreviation exists in our map 
        String country1Check = countryAbb.get(country1);
        String country2Check = countryAbb.get(country2);
		// if they aren't int the map of abbr either then we return -1 
        if (country1Check == null || country2Check == null) {
            return -1; 
        }
		// now we must check if the distances of the countries are valid 
        int distance = distances_wCapitals.getOrDefault(country1Check + "_" + country2Check, -1);
        if (distance == -1) {
            distance = distances_wCapitals.getOrDefault(country1Check + "_" + country2Check, -1);
        }

        return distance < 0 ? -1 : distance; // Return found distance or -1 if not found
    }

    public List<String> findPath(String country1, String country2) {
		// if country1 is the same as country2 there's no distance so report we're already there 
		if (country1.equals(country2)) {
			List<String> sameCountryPath = new ArrayList<>();
			sameCountryPath.add(country1 + " --> " + country2 + " (0 km.)");
			return sameCountryPath;
		}
		// if the country doesn't exist or there is an invalid input we need to create a new arr 
        if (!countryBorders.containsKey(country1) || !countryBorders.containsKey(country2)) {
            return new ArrayList<>(); 
        }
		// initalize data structures -> source geeksforgeeks priorityQueues and Dijkstras Algorithm 
        PriorityQueue<Map.Entry<String, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        Map<String, Integer> ReportDistances = new HashMap<>();
        Map<String, String> ParentCountry = new HashMap<>();
		// want the maximum int if 1.3 -> 1 km
        for (String countryName : countryBorders.keySet()) {
            ReportDistances.put(countryName, Integer.MAX_VALUE);
        }
		// add the entry to the queue and add the distance to the hashmap
        queue.add(new AbstractMap.SimpleEntry<>(country1, 0));
        ReportDistances.put(country1, 0);
		// while their are still countries (borders) in the queue we must get 
		// the correct path. So while we have neighboring countries (countryBorders)
		// we will iterate through gathering the distance of neighbors to neighboring countries
		// logging their distances and updating their parents as we get further from countryA to countryB 
        while (!queue.isEmpty()) {
            Map.Entry<String, Integer> currCountry = queue.poll();
            String currCountryName = currCountry.getKey();
            int currDistance = currCountry.getValue();

            if (currCountryName.equals(country2)) {
                break;
            } else {
				List<String> neighborCountries = countryBorders.get(currCountryName);
				if (neighborCountries != null) {
					for (String neighbor : neighborCountries) {
						int distance = getDistance(currCountryName, neighbor);
						if (distance != -1) { 
							int totalDistance = currDistance + distance;
							if (totalDistance < ReportDistances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
								ReportDistances.put(neighbor, totalDistance);
								ParentCountry.put(neighbor, currCountryName);
								queue.add(new AbstractMap.SimpleEntry<>(neighbor, totalDistance));
							}
						}
					}
				}
			}
		}
		// we will now start path building initalizing our steps between the given countries 
		// we must see if the parent country exists and calculate the distance between parent 
		// and countryB using the getDistance(). When we finish the loop we have to reverse the path so 
		// it's presented in teh correct order from start to destination
        List<String> path = new ArrayList<>();
        String countryB = country2;
        while (countryB != null) {
            String Parent_country = ParentCountry.get(countryB);
            if (Parent_country != null) {
                int countryDist = getDistance(Parent_country, countryB); 
                if (countryDist != -1) {
                    path.add(Parent_country + " --> " + countryB + " (" + countryDist + " km.)");
                } else {
                    path.add(Parent_country + " --> " + countryB + " (Distance unknown)");
                }
            }
            countryB = Parent_country;
        }
		
        Collections.reverse(path);
        return path;
    }

    public void acceptUserInput() {
		// keyboard input 
        Scanner scanner = new Scanner(System.in);

        while (true) {
			// get the first country 
            System.out.print("Enter the name of the first country (type EXIT to quit): ");
            String country1 = scanner.nextLine().trim();

            if (country1.equalsIgnoreCase("EXIT")) {
                break;
            }
			// if the country doesn't exist in our hashmap of countryBorders it's invalid 
            if (!countryBorders.containsKey(country1)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }
			// if an invalid case is entered we must ask for a valid input until we can move onwards 
            while (true) {
				System.out.print("Enter the name of the second country (type EXIT to quit): ");
				String country2 = scanner.nextLine().trim();

				if (country2.equalsIgnoreCase("EXIT")) {
					break;
				}

				if (!countryBorders.containsKey(country2)) {
					System.out.println("Invalid country name. Please enter a valid country name.");
					continue;
				}
				// initalize our path and save the path returned from findPath
				List<String> path = findPath(country1, country2);
				// if there's no path then there's no valid path so report that
				// else print our each step in the path (traverse the list) 
				if (path.isEmpty()) {
					System.out.println("No valid path exists between " + country1 + " and " + country2);
				} else {
					System.out.println("Route from " + country1 + " to " + country2 + ":");
					for (String step : path) {
						System.out.println("* " + step);
					}
				}

				break;
			}
		}
		// close the scanner
		scanner.close();
	}

    public static void main(String[] args) {
		// check to make sure the correct number of args were provided else exit 
		if (args.length != 3) {
			System.out.println("Invalid number of args provided.");
			return;
		} else {
			// create obj of IRoadTrip calling constructor and using it to get userinput 
			IRoadTrip a3 = new IRoadTrip(args);
			a3.acceptUserInput();
		}
    }

}