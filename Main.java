import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        CompletableFuture<int[]> originalArrayFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Generating array...");
            return generateRandomArray(10, 1, 101);
        });

        CompletableFuture<int[]> modifiedArrayFuture = originalArrayFuture.thenApplyAsync(array -> {
            System.out.println("Modifying array by adding 5 to each element...");
            return modifyArray(array, 5);
        });

        CompletableFuture<BigInteger> factorialFuture = originalArrayFuture.thenCombineAsync(
            modifiedArrayFuture,
            (originalArray, modifiedArray) -> {
                System.out.println("Calculating factorial of the sum...");
                int totalSum = Arrays.stream(originalArray).sum() + Arrays.stream(modifiedArray).sum();
                return factorial(totalSum);
            }
        );

        CompletableFuture<Void> displayFuture = CompletableFuture.allOf(
            originalArrayFuture.thenAcceptAsync(array -> System.out.println("Original array: " + Arrays.toString(array))),
            modifiedArrayFuture.thenAcceptAsync(array -> System.out.println("Modified array: " + Arrays.toString(array))),
            factorialFuture.thenAcceptAsync(result -> System.out.println("Factorial calculated: " + result))
        );

        displayFuture.get();

        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime) + " ms");
    }

    private static int[] generateRandomArray(int size, int min, int max) {
        return new Random().ints(size, min, max).toArray();
    }

    private static int[] modifyArray(int[] array, int increment) {
        return Arrays.stream(array).map(x -> x + increment).toArray();
    }

    private static BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
