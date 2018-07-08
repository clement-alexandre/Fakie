package com.fakie.utils.paprika;

import com.fakie.io.input.apk.APKInfo;
import com.fakie.io.input.apk.CSVInfoReader;
import com.fakie.utils.exceptions.FakieException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import paprika.analyzer.SootAnalyzer;
import paprika.entities.PaprikaApp;
import paprika.neo4j.DatabaseManager;
import paprika.neo4j.ModelToGraph;
import paprika.neo4j.QueryEngine;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class PaprikaAccessor {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final String[] EXT = new String[]{"apk"};

    public void analyse(File androidJars, File apk, File info, Path db) throws FakieException {
        APKInfo apkInfo = new CSVInfoReader().readInfo(info);
        ModelToGraph modelToGraph = new ModelToGraph(db.toString());
        if (apk.isDirectory()) {
            analyseDirectory(androidJars, apk, db, apkInfo, modelToGraph);
        }
        else {
            analyseApk(androidJars, apk, db, apkInfo.get(apk.getName()), modelToGraph);
        }
        close(modelToGraph);
    }

    public void query(Path db) {
        QueryEngine queryEngine = new QueryEngine(db.toString());
    }

    private void analyseApk(File androidJars, File apk, Path db, APKInfo.Entry entry, ModelToGraph modelToGraph) {
        try {
            logger.info("Paprika analyse %s", apk);
            SootAnalyzer analyzer = new SootAnalyzer(
                    apk.toPath().toString(),
                    androidJars.toPath().toString(),
                    entry.getName(), entry.getPkg(), entry.getPkg(),
                    date(), 1, "default-developer", "default-category",
                    "Free", 1, "0", "", "", "", "", true
            );
            analyzer.init();
            analyzer.runAnalysis();
            logger.info("Saving Paprika analysis into %s", db);
            PaprikaApp paprikaApp = analyzer.getPaprikaApp();
            logger.debug("Creating graph for %s", paprikaApp);
            modelToGraph.insertApp(paprikaApp);
            logger.info("Successfully saved Paprika db");
        }
        catch (Exception e) {
            logger.error(e);
        }
    }

    private void analyseDirectory(File androidJars, File dir, Path db, APKInfo apkInfo, ModelToGraph modelToGraph) {
        logger.info("Paprika analysing apk in the %s folder", dir);
        Collection<File> files = FileUtils.listFiles(dir, EXT, false);
        for (File apk : files) {
            analyseApk(androidJars, apk, db, apkInfo.get(apk.getName()), modelToGraph);
        }
        logger.info("Paprika successfully analyzed the apk folder %s", dir);
    }

    private String date() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private void close(ModelToGraph modelToGraph) throws PaprikaException {
        try {
            Object field = FieldUtils.readField(modelToGraph, "graphDatabaseService", true);
            GraphDatabaseService db = (GraphDatabaseService) field;
            db.shutdown();
            field = FieldUtils.readField(modelToGraph, "databaseManager", true);
            DatabaseManager manager = (DatabaseManager) field;
            manager.shutDown();
        }
        catch (IllegalAccessException e) {
            throw new PaprikaException(e);
        }
    }
}
