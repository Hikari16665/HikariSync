package me.eventually.hikarisync.core.logging;

import java.util.logging.Logger;

public class Banner {
    private static final String banner = """

    ██╗  ██╗██╗██╗  ██╗ █████╗ ██████╗ ██╗    ███████╗██╗   ██╗███╗   ██╗ ██████╗\s
    ██║  ██║██║██║ ██╔╝██╔══██╗██╔══██╗██║    ██╔════╝╚██╗ ██╔╝████╗  ██║██╔════╝\s
    ███████║██║█████╔╝ ███████║██████╔╝██║    ███████╗ ╚████╔╝ ██╔██╗ ██║██║    \s
    ██╔══██║██║██╔═██╗ ██╔══██║██╔══██╗██║    ╚════██║  ╚██╔╝  ██║╚██╗██║██║    \s
    ██║  ██║██║██║  ██╗██║  ██║██║  ██║██║    ███████║   ██║   ██║ ╚████║╚██████╗\s
    ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝    ╚══════╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝\s

    Cross-server data synchronization framework by Eventually""";
    public static void printBanner(Logger logger) {
        logger.info(banner);
    }
}
