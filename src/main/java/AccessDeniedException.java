public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super("Access " + s + " denied");
    }
}
