ReadMe:
	I had such a hard time with this project. At first I was doing well:
	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.*;

	class CountryNameMapper {
		private Map<String, String> countryAliases;
		private Map<String, String> endDates;

		public CountryNameMapper() {
			countryAliases = new HashMap<>();
			endDates = new HashMap<>();
		}

		public void addCountryAlias(String alias, String mainCountryName) {
			countryAliases.put(alias, mainCountryName);
		}

		public String getStandardizedCountryName(String countryName) {
			return countryAliases.getOrDefault(countryName, countryName);
		}

		public Map<String, String> getCountryAliases() {
			return countryAliases;
		}

		public Map<String, String> getEndDates() {
			return endDates;
		}

		public void addAlias(String alias, String mainCountryName) {
			countryAliases.put(alias, mainCountryName);
		}

		public void addEndDate(String countryName, String endDate) {
			endDates.put(countryName, endDate);
		}
	}
	I had this as another class in my code to handle alias but in the end I scrapped 
	the idea because I ran out of time. Balancing dual projects at the same time with 
	1 day in between due dates was rough. It was unfortunate that CS221 and CS245 had 
	projects the same week with assignments prior during finals week. So overall I would
	probably use this type of set up if I was to hard code the outlier cases. I knew we shouldn't
	hardcode and it would get messy but my idea was to just add all the alias to fall under 
	the same key and then whichever is mentioned in the user input the main country name 
	would be used for example: JPN -> Japan, or US -> United States -> United States of America -> 2
	I had a more complex code originally but messed up my logic when I went purely off of countryIDs
	private void readCapitalDist(String fileName) {
    	 try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("numa")) {
					continue;
				}
				String[] parts = line.split(",");
				String countryA = parts[0].trim(); // 2
				String countryB = parts[2].trim();
				int distance = Integer.parseInt(parts[4].trim());
				String standardizedCountryA = countryNameMapper.getStandardizedCountryName(countryA);
				String standardizedCountryB = countryNameMapper.getStandardizedCountryName(countryB);
				
				Map<String, String> countryIds = countryNameMapper.getCountryIds();

				if (countryIds.containsKey(standardizedCountryA)) {
					standardizedCountryA = countryIds.get(standardizedCountryA);
				}
				if (countryIds.containsKey(standardizedCountryB)) {
					standardizedCountryB = countryIds.get(standardizedCountryB);
				}

				if (!countryBorders.containsKey(standardizedCountryA)) {
					countryBorders.put(standardizedCountryA, new HashMap<>());
				}
				if (!countryBorders.containsKey(standardizedCountryB)) {
					countryBorders.put(standardizedCountryB, new HashMap<>());
				}

				Map<String, Integer> distancesA = countryBorders.get(standardizedCountryA);
				distancesA.put(standardizedCountryB, distance);
				countryBorders.put(standardizedCountryA, distancesA);

				Map<String, Integer> distancesB = countryBorders.get(standardizedCountryB);
				distancesB.put(standardizedCountryA, distance);
				countryBorders.put(standardizedCountryB, distancesB);

				countriesInDistances.add(standardizedCountryA);
				countriesInDistances.add(standardizedCountryB);
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStateName(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String countryID = parts[0].trim();
                StringBuilder countryBuilder = new StringBuilder();
				for (int i = 2; i < parts.length - 2; i++) {
					countryBuilder.append(parts[i]).append(" ");
				}
				String countryName = countryBuilder.toString().trim();
                String endDate = parts[4].trim();
				
				
                if (endDate.isEmpty() || endDate.compareTo("2020-12-31") > 0) {
					countryNameMapper.addCountryAlias(countryID, countryName);
					countryNameMapper.addCountryID(countryID, countryName);
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
	Overall it was rough due to the data descrepancies but was a taste at the real world 
	since nothing comes pretty. I tried really hard, I just didn't meet the time constraints 
	so I'm kinda depressed knowing my grade is going down because of the handling edge cases. 
	I hope my efforts are reflected in my code, and I can see the portions from where I can 
	improve as well. 