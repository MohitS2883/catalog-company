import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class ShamirSecretSharing {

    // Function to decode the y value given its base and value
    public static BigInteger decode(String baseStr, String valueStr) {
        int base = Integer.parseInt(baseStr);
        return new BigInteger(valueStr, base);  
    }

    // Lagrange Interpolation function to calculate f(0) i.e., constant term 'c'
    public static BigDecimal lagrangeInterpolation(List<BigInteger[]> points, int k) {
        BigDecimal result = BigDecimal.ZERO;
        MathContext mc = new MathContext(50);  

        for (int i = 0; i < k; i++) {
            BigDecimal term = new BigDecimal(points.get(i)[1], mc);  // y value

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigDecimal numerator = BigDecimal.ZERO.subtract(new BigDecimal(points.get(j)[0], mc));  // 0 - x_j
                    BigDecimal denominator = new BigDecimal(points.get(i)[0].subtract(points.get(j)[0]), mc);  // x_i - x_j
                    term = term.multiply(numerator, mc).divide(denominator, mc);  // term *= (0 - x_j) / (x_i - x_j)
                }
            }
            result = result.add(term, mc);  
        }
        return result;
    }

    public static void main(String[] args) {
        try {
             = new JSONParser();
            Object obj = parser.parse(new FileReader("\\input.json"));
            JSONObject jsonObject = (JSONObject) obj;

            // Extract n and k from the "keys" object
            JSONObject keys = (JSONObject) jsonObject.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());

            // List to hold (x, y) pairs
            List<BigInteger[]> points = new ArrayList<>();

            // Extract the points from the JSON
            for (Object key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    BigInteger x = new BigInteger((String) key);  // Parse x as BigInteger
                    JSONObject rootData = (JSONObject) jsonObject.get(key);
                    String base = (String) rootData.get("base");
                    String value = (String) rootData.get("value");
                    BigInteger y = decode(base, value);  // Decode the y value
                    points.add(new BigInteger[]{x, y});
                }
            }

            // Calculate the constant term 'c' using Lagrange Interpolation
            BigDecimal constant = lagrangeInterpolation(points, k);
            System.out.println("The constant term c is: " + constant.toBigInteger());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
