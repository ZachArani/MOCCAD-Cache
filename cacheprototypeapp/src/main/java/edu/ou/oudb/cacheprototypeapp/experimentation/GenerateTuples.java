package edu.ou.oudb.cacheprototypeapp.experimentation;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.List;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONLoader;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONParser;

public class GenerateTuples {

    String[] firstNames;
    String[] lastNames;
    String[] descriptions;
    int total = 0;
    int current = 1;
    GregorianCalendar gc;
    String tableName;

    String url = "http://10.204.69.210:8080/CloudWebService/rest/result?query=";

    public GenerateTuples(String tableName, int total)
    {
        firstNames = new String[]{"David", "John", "Paul", "Mark", "James", "Andrew", "Scott", "Steven", "Robert", "Stephen", "William", "Craig", "Michael", "Stuart", "Christopher", "Alan", "Colin", "Brian", "Kevin", "Gary", "Richard", "Derek", "Martin", "Thomas", "Neil", "Barry", "Ian", "Jason", "Iain", "Gordon", "Alexander", "Graeme", "Peter", "Darren", "Graham", "George", "Kenneth", "Allan", "Simon", "Douglas", "Keith", "Lee", "Anthony", "Grant", "Ross", "Jonathan", "Gavin", "Nicholas", "Joseph", "Stewart", "Daniel", "Edward", "Matthew", "Donald", "Fraser", "Garry", "Malcolm", "Charles", "Duncan", "Alistair", "Raymond", "Philip", "Ronald", "Ewan", "Ryan", "Francis", "Bruce", "Patrick", "Alastair", "Bryan", "Marc", "Jamie", "Hugh", "Euan", "Gerard", "Sean", "Wayne", "Adam", "Calum", "Alasdair", "Robin", "Greig", "Angus", "Russell", "Cameron", "Roderick", "Norman", "Murray", "Gareth", "Dean", "Eric", "Adrian", "Gregor", "Samuel", "Gerald", "Henry", "Justin", "Benjamin", "Shaun", "Callum", "Campbell", "Frank", "Roy", "Timothy", "Glen", "Marcus", "Hamish", "Niall", "Barrie", "Liam", "Brendan", "Terence", "Greg", "Leslie", "Lindsay", "Trevor", "Vincent", "Christian", "Lewis", "Rory", "Antony", "Fergus", "Roger", "Arthur", "Dominic", "Ewen", "Jon", "Owen", "Gregory", "Jeffrey", "Terry", "Damian", "Geoffrey", "Harry", "Walter", "Bernard", "Desmond", "Jack", "Aaron", "Archibald", "Blair", "Jeremy", "Nathan", "Alister", "Dale", "Dylan", "Glenn", "Julian", "Leon", "Allen", "Martyn", "Nigel", "Alisdair", "Denis", "Drew", "Evan", "Phillip", "Frazer", "Guy", "Laurence", "Lawrence", "Magnus", "Crawford", "Finlay", "Frederick", "Gregg", "Karl", "Kerr", "Mohammed", "Rodney", "Victor", "Carl", "Daryl", "Don", "Edwin", "Erik", "Grahame", "Ivan", "Kyle", "Leigh", "Lorne", "Maurice", "Murdo", "Nicolas", "Steve", "Allister", "Clark", "Darran", "Dennis", "Elliot", "Leonard", "Nairn", "Scot", "Stefan", "Toby", "Warren", "Billy", "Clive", "Damien", "Louis", "Mohammad", "Neill", "Noel", "Ralph", "Sandy", "Albert", "Alun", "Brett", "Clifford", "Eoin", "Glyn", "Imran", "Ivor", "Johnathan", "Kevan", "Neal", "Oliver", "Robbie", "Roland", "Stanley", "Aidan", "Antonio", "Austin", "Bradley", "Cornelius", "Darrin", "Derrick", "Innes", "Kristian", "Lachlan", "Mathew", "Moray", "Nicol", "Shane", "Tony", "Brent", "Findlay", "Forbes", "Gilbert", "Giles", "Jay", "Kelvin", "Leighton", "Marco", "Omar", "Roddy", "Tom", "Abdul", "Alfred", "Alick", "Ashley", "Bryce", "Conrad", "Darryl", "Eugene", "Harold", "Harvey", "Hector", "Jody", "Kieran", "Kirk", "Kris", "Marshall", "Muhammad", "Ramsay", "Ray", "Rodger", "Seumas", "Tommy", "Wai", "Alex", "Ali", "Andrea", "Archie", "Daren", "Derick", "Gideon", "Jan", "Juan", "Kerry", "Kieron", "Luke", "Lyall", "Manus", "Marvin", "Morgan", "Muir", "Myles", "Ronnie", "Rowan", "Rupert", "Spencer", "Stephan", "Struan", "Torquil", "Wallace", "Aftab", "Alain", "Alec", "Alvin", "Anton", "Arran", "Arron", "Austen", "Aynsley", "Benedict", "Chad", "Chun", "Clarke", "Damon", "Danny", "Darron", "Declan", "Deryck", "Edmond", "Edmund", "Jacob", "Johnston", "Keiron", "Kennedy", "Khalil", "Kristofer", "Laurie", "Lloyd", "Mario", "Max", "Maxwell", "Mitchell", "Morris", "Nathaniel", "Naveed", "Neville", "Nickolas", "Piers", "Quentin", "Rennie", "Reuben", "Riccardo", "Roberto", "Ruaraidh", "Ruaridh", "Stefano", "Symon", "Tobias", "Todd", "Abid", "Adnan", "Aeneas", "Aiden", "Ainslie", "Ajay", "Alessandro", "Alyn", "Anderson", "Andre", "Ashok", "Asif", "Atholl", "Bjorn", "Brandon", "Brydon", "Bryn", "Caine", "Calvin", "Carlo", "Ceri", "Chris", "Christien", "Claudio", "Clayton", "Clint", "Connell", "Cyril", "Damion", "Darin", "Dario", "Darroch", "Deryk", "Dirk", "Donovan", "Dustin", "Eamonn", "Edgar", "Elliott", "Elton", "Emlyn", "Eoghan", "Erlend", "Farooq", "Garth", "Geoff", "Gerrard", "Gerry", "Giancarlo", "Gidon", "Grierson", "Hamilton", "Hans", "Hendry", "Howard", "Irvine", "Jaimie", "Jarad", "Jayson", "Jean", "Jeff", "Jerome", "Joel", "Jude", "Kane", "Karan", "Karim", "Kashif", "Keiran", "Kendon", "Kent", "Kwok", "Laith", "Lauchlan", "Leo", "Leyton", "Lindsey", "Logan", "Lorn", "Lyle", "Mason", "Mervyn", "Michel", "Mubarak", "Mungo", "Murdoch", "Nathanael", "Neall", "Nickie", "Nicky", "Nikki", "Nikolas", "Paolo", "Perry", "Ranald", "Rehan", "Ricky", "Rikki", "Ritchie", "Rizwan", "Robertson", "Roderic", "Rolf", "Ronan", "Rowland", "Sam", "Scotland", "Seth", "Shahid", "Shakeel", "Sidney", "Sinclair", "Sonny", "Taylor", "Tin", "Tomas", "Travis", "Tristan", "Vernon", "Vince", "Waheed", "Waseem", "Wei", "Wilson", "Yan", "Zak", "Aamir", "Abdullahi", "Abdulrazak", "Abraham", "Adebayo", "Adel", "Adrain", "Adriano", "Ahmad", "Ahmed", "Aidon", "Ajeet", "Al-Motamid", "Alaistair", "Alberto", "Aldo", "Aldous", "Alen", "Alexandre", "Alfredo", "Alisteir", "Allon", "Alton", "Alwyn", "Aman", "Amanda", "Amato", "Amir", "Amit", "Amitabha", "Amos", "Anand", "Anant", "Anastasi", "Anastasio", "Anastasios", "Andres", "Angel", "Angelo", "Anil", "Anjam", "Anjum", "Ann", "Antonius", "Anwar", "Aonghas", "Aonghus", "Aqif", "Ardene", "Ardle", "Ari", "Arif", "Arlyn", "Armando", "Armond", "Arne", "Arnout", "Arol", "Aron", "Aroon", "Arshid", "Arvid", "Arvind", "Asa", "Asad", "Asaf", "Asam", "Ashiqhusein", "Ashwani", "Asrar", "Athol", "Avees", "Ayham", "Ayokoladele", "Ayron", "Azzam", "Balfour", "Balraj", "Barbara", "Barnabas", "Barron", "Bartholomew", "Basil", "Bassam", "Bayne", "Ben-John", "Bengiman", "Benoit", "Bernardo", "Bevan", "Bill", "Bllal", "Blythe", "Bobby", "Brad", "Brant", "Brook", "Bryden", "Byram", "Byron", "Caleb", "Callam", "Carey", "Carol", "Carreen", "Cary", "Casey", "Cathorne", "Chae", "Charanjeev", "Che", "Chee", "Chi", "Chincdu", "Christan", "Christie", "Christos", "Chrys", "Chu", "Churnthoor", "Ciaran", "Ciaron", "Ciobhan", "Claus", "Cliff", "Clinton", "Colan", "Coll", "Collin", "Colum", "Con", "Connor", "Corey", "Corin", "Cormac", "Corren", "Cowan", "Craige", "Cullen", "Daiman", "Daljit", "Dall", "Dameon", "Damyon", "Danga", "Danielle", "Darius", "Daron", "Darrel", "Darrell", "Darryll", "Darryn", "Davide", "Davidson", "Davinder", "Davud", "Davyd", "Dawson", "Dax", "Del", "Dell", "Denver", "Dermot", "Derry", "Derryl", "Derryn", "Diarmid", "Diauddin", "Dick", "Diego", "Dino", "Dion", "Dolan", "Domenico", "Domenyk", "Donn", "Donnie", "Donny", "Dorian", "Dorino", "Dougal", "Dowell", "Duane", "Dugald", "Dulip", "Dylaan", "Eamon", "Ean", "Earl", "Eben", "Edoardino", "Efeoni", "Egidio", "Eiichi", "El", "Elgin", "Ellis", "Eloson", "Emmanuel", "Emran", "Emrys", "Eoghann", "Erkan", "Erl", "Ernest", "Erwin", "Esmond", "Ewing", "Fabio", "Feargus", "Felix", "Ferzund", "Finbar", "Finley", "Fionn", "Francesco", "Francisco", "Francois", "Frankie", "Frazier", "Fu", "Gabriel", "Galen", "Gallvin", "Gardner", "Garreth", "Garry-John", "Gawad", "Geore", "Georges", "Georgio", "Gerarde", "Gerhard", "Gethin", "Ghassan", "Gianni", "Gillan", "Gilmour", "Ginno", "Gino", "Giovanni", "Glynn", "Godfrey", "Grainger", "Greggory", "Gren", "Grigor", "Guido", "Gurbal", "Gurchinchel", "Gurhimmat", "Gurjeet", "Gurkimat", "Gurmeet", "Gurvinder", "Gustavus", "Gwion", "Haitham", "Hani", "Hardeep", "Hassan", "Hatim", "Heather", "Hedley", "Hilton", "Himesh", "Hitesh", "Hoi-Yuen", "Hoy", "Hsin", "Hui", "Hussain", "Hytham", "Ifeatu", "Iffor", "Ilya", "Imeobong", "Imtiaz", "Iqbal", "Isaac", "Isabirye", "Ishar", "Islay", "Ivy", "Jackie", "Jackson", "Jaco", "Jade", "Jagan", "Jagdeep", "Janet", "Jardine", "Jarno", "Jasbir", "Jasen", "Jasjeet", "Jaspal", "Jaspar", "Jatinder", "Javaid", "Jean-Baptiste", "Jedd", "Jeffery", "Jeremiah", "Jeroen-Hans", "Jerry", "Jesse", "Jim", "Jimmy", "Jimsheed", "Joao", "Jodie", "Joe", "Johan", "Johann", "John-Paul", "Johnny", "Jojeph", "Jonathon", "Jonson", "Jose", "Josep-Ramon", "Joshua", "Joss", "Jreen", "Judd", "Julie", "Julyan"};
        lastNames = new String[]{"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson", "White", "Lopez", "Lee", "Gonzalez", "Harris", "Clark", "Lewis", "Robinson", "Walker", "Perez", "Hall", "Young", "Allen", "Sanchez", "Wright", "King", "Scott", "Green", "Baker", "Adams", "Nelson", "Hill", "Ramirez", "Campbell", "Mitchell", "Roberts", "Carter", "Phillips", "Evans", "Turner", "Torres", "Parker", "Collins", "Edwards", "Stewart", "Flores", "Morris", "Nguyen", "Murphy", "Rivera", "Cook", "Rogers", "Morgan", "Peterson", "Cooper", "Reed", "Bailey", "Bell", "Gomez", "Kelly", "Howard", "Ward", "Cox", "Diaz", "Richardson", "Wood", "Watson", "Brooks", "Bennett", "Gray", "James", "Reyes", "Cruz", "Hughes", "Price", "Myers", "Long", "Foster", "Sanders", "Ross", "Morales", "Powell", "Sullivan", "Russell", "Ortiz", "Jenkins", "Gutierrez", "Perry", "Butler", "Barnes", "Fisher", "Henderson", "Coleman", "Simmons", "Patterson", "Jordan", "Reynolds", "Hamilton", "Graham", "Kim", "Gonzales", "Alexander", "Ramos", "Wallace", "Griffin", "West", "Cole", "Hayes", "Chavez", "Gibson", "Bryant", "Ellis", "Stevens", "Murray", "Ford", "Marshall", "Owens", "Mcdonald", "Harrison", "Ruiz", "Kennedy", "Wells", "Alvarez", "Woods", "Mendoza", "Castillo", "Olson", "Webb", "Washington", "Tucker", "Freeman", "Burns", "Henry", "Vasquez", "Snyder", "Simpson", "Crawford", "Jimenez", "Porter", "Mason", "Shaw", "Gordon", "Wagner", "Hunter", "Romero", "Hicks", "Dixon", "Hunt", "Palmer", "Robertson", "Black", "Holmes", "Stone", "Meyer", "Boyd", "Mills", "Warren", "Fox", "Rose", "Rice", "Moreno", "Schmidt", "Patel", "Ferguson", "Nichols", "Herrera", "Medina", "Ryan", "Fernandez", "Weaver", "Daniels", "Stephens", "Gardner", "Payne", "Kelley", "Dunn", "Pierce", "Arnold", "Tran", "Spencer", "Peters", "Hawkins", "Grant", "Hansen", "Castro", "Hoffman", "Hart", "Elliott", "Cunningham", "Knight", "Bradley", "Carroll", "Hudson", "Duncan", "Armstrong", "Berry", "Andrews", "Johnston", "Ray", "Lane", "Riley", "Carpenter", "Perkins", "Aguilar", "Silva", "Richards", "Willis", "Matthews", "Chapman", "Lawrence", "Garza", "Vargas", "Watkins", "Wheeler", "Larson", "Carlson", "Harper", "George", "Greene", "Burke", "Guzman", "Morrison", "Munoz", "Jacobs", "Obrien", "Lawson", "Franklin", "Lynch", "Bishop", "Carr", "Salazar", "Austin", "Mendez", "Gilbert", "Jensen", "Williamson", "Montgomery", "Harvey", "Oliver", "Howell", "Dean", "Hanson", "Weber", "Garrett", "Sims", "Burton", "Fuller", "Soto", "Mccoy", "Welch", "Chen", "Schultz", "Walters", "Reid", "Fields", "Walsh", "Little", "Fowler", "Bowman", "Davidson", "May", "Day", "Schneider", "Newman", "Brewer", "Lucas", "Holland", "Wong", "Banks", "Santos", "Curtis", "Pearson", "Delgado", "Valdez", "Pena", "Rios", "Douglas", "Sandoval", "Barrett", "Hopkins", "Keller", "Guerrero", "Stanley", "Bates", "Alvarado", "Beck", "Ortega", "Wade", "Estrada", "Contreras", "Barnett", "Caldwell", "Santiago", "Lambert", "Powers", "Chambers", "Nunez", "Craig", "Leonard", "Lowe", "Rhodes", "Byrd", "Gregory", "Shelton", "Frazier", "Becker", "Maldonado", "Fleming", "Vega", "Sutton", "Cohen", "Jennings", "Parks", "Mcdaniel", "Watts", "Barker", "Norris", "Vaughn", "Vazquez", "Holt", "Schwartz", "Steele", "Benson", "Neal", "Dominguez", "Horton", "Terry", "Wolfe", "Hale", "Lyons", "Graves", "Haynes", "Miles", "Park", "Warner", "Padilla", "Bush", "Thornton", "Mccarthy", "Mann", "Zimmerman", "Erickson", "Fletcher", "Mckinney", "Page", "Dawson", "Joseph", "Marquez", "Reeves", "Klein", "Espinoza", "Baldwin", "Moran", "Love", "Robbins", "Higgins", "Ball", "Cortez", "Le", "Griffith", "Bowen", "Sharp", "Cummings", "Ramsey", "Hardy", "Swanson", "Barber", "Acosta", "Luna", "Chandler", "Blair", "Daniel", "Cross", "Simon", "Dennis", "Oconnor", "Quinn", "Gross", "Navarro", "Moss", "Fitzgerald", "Doyle", "Mclaughlin", "Rojas", "Rodgers", "Stevenson", "Singh", "Yang", "Figueroa", "Harmon", "Newton", "Paul", "Manning", "Garner", "Mcgee", "Reese", "Francis", "Burgess", "Adkins", "Goodman", "Curry", "Brady", "Christensen", "Potter", "Walton", "Goodwin", "Mullins", "Molina", "Webster", "Fischer", "Campos", "Avila", "Sherman", "Todd", "Chang", "Blake", "Malone", "Wolf", "Hodges", "Juarez", "Gill", "Farmer", "Hines", "Gallagher", "Duran", "Hubbard", "Cannon", "Miranda", "Wang", "Saunders", "Tate", "Mack", "Hammond", "Carrillo", "Townsend", "Wise", "Ingram", "Barton", "Mejia", "Ayala", "Schroeder", "Hampton", "Rowe", "Parsons", "Frank", "Waters", "Strickland", "Osborne", "Maxwell", "Chan", "Deleon", "Norman", "Harrington", "Casey", "Patton", "Logan", "Bowers", "Mueller", "Glover", "Floyd", "Hartman", "Buchanan", "Cobb", "French", "Kramer", "Mccormick", "Clarke", "Tyler", "Gibbs", "Moody", "Conner", "Sparks", "Mcguire", "Leon", "Bauer", "Norton", "Pope", "Flynn", "Hogan", "Robles", "Salinas", "Yates", "Lindsey", "Lloyd", "Marsh", "Mcbride", "Owen", "Solis", "Pham", "Lang"};
        descriptions = new String[]{"Analysis%20of%20Body%20Flu", "Biopsy", "Endoscopy", "Genetic%20Testing", "Imaging", "Measurement%20of%20Body"};
        this.total = total;
        this.tableName = tableName;
        gc = new GregorianCalendar();
    }

    public void generate()
    {
        try {
            process("CREATE TABLE " + tableName + "(noteid INT, patientfirstname STRING, patientlastname STRING, doctorfirstname STRING, doctorlastname STRING, description STRING, p_date_time TIMESTAMP, heartrate INT);");
            process(getTuples());
        } catch(JSONParserException | DownloadDataException e) {}
    }

    public String getTuples()
    {
        String result = "INSERT INTO TABLE " + tableName + " VALUES ";
        while(current <= total)
        {
            result += "(" + generateID() + ", "
                    + generateFirstName() + ", " + generateLastName() + ", "
                    + generateFirstName() + ", " + generateLastName() + ", "
                    + generateDescription() + ", "
                    + generateDate() +", "
                    + generateHeartrate() + "), ";
        }
        return result.substring(0, result    String url = "http://10.204.69.210:8080/CloudWebService/rest/result?query=";
.length()-1) + ";"; //Need to get rid of final comma.
    }

    private String generateFirstName()
    {
        return "%27" + firstNames[(int)(Math.random() * 500)] + "%27";

    }

    private String generateLastName()
    {
        return "%27" + lastNames[(int)(Math.random() * 500)] + "%27";
    }

    private String generateID()
    {
        return String.valueOf(current++);
    }

    private String generateHeartrate()
    {
        return String.valueOf((int)(Math.random() * 185) + 15);
    }

    private String generateDescription()
    {
        return "%27" + descriptions[(int)(Math.random() * 5)] + "%27";
    }

    private String generateDate()
    {
        int year = randBetween(1940, 2014);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.YEAR, year);
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        int hours = (int)(Math.random() * 24);
        int minutes = (int)(Math.random() * 60);
        return "%27" + gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH) + "%20" + String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + "00%27";
    }

    private int randBetween(int start, int end)
    {
        return start + (int)(Math.round(Math.random() * (end - start)));
    }

    public List<List<String>> process(String query) throws JSONParserException, DownloadDataException {

        QuerySegment querySegment = null;
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(query
                .replace(" ", "%20")
                .replace("<", "%3C")
                .replace(">","%3E")
                .replace(";", ""));
        String urlString = sb.toString();

        InputStream jsonStream = JSONLoader.getJSONInputStreamFromUrl(urlString);

        JSONParser.QueryResult result;
        try {
            result = JSONParser.parseQueryResult(jsonStream);
            if (result == null)
            {
                throw new JSONParserException();
            }
        } catch (IOException e) {
            throw new JSONParserException(e.getMessage());
        }
        querySegment = new QuerySegment(result.tuples);
        return querySegment.getTuples();

    }


}
