package org.cosmic.ide.project;

public class CodeTemplate {

    public static String getJavaClassTemplate(
            String packageName, String className, boolean isCreateMainMethod) {
        var header = "";
        if (!isEmpty(packageName)) {
            header = "package " + packageName + ";\n" + "\n";
        }
        return header
                + "import java.util.*;\n\n"
                + "public class "
                + className
                + " {\n"
                + (isCreateMainMethod
                        ? "    public static void main(String[] args) {\n"
                                + "        System.out.println(\"Hello, World!\");\n"
                                + "    }"
                        : "    ")
                + "\n"
                + "}\n";
    }

    public static String getKotlinClassTemplate(
            String packageName, String className, boolean isCreateMainMethod) {
        var header = "";
        if (!isEmpty(packageName)) {
            header = "package " + packageName + "\n" + "\n";
        }
        return header
                + "import java.util.*\n\n"
                + "class "
                + className
                + " {\n"
                + (isCreateMainMethod
                        ? "    fun main(args: Array<String>) {\n"
                                + "        println(\"Hello, World!\")\n"
                                + "    }"
                        : "    ")
                + "\n"
                + "}\n";
    }

    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }
}
