package ru.via.decode.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class DecodeCache {
    private static final Logger logger = LoggerFactory.getLogger(DecodeCache.class);

    private String selectSystemIdentifier = "";
    private String selectCatalogIdentifier = "";

    private LoadingCache<String, String> decodeIdentifierCache;
    private LoadingCache<String, List<DecodeRecord>> decodeRecordCache;

    @Value("${decode.cache.maximumSize}")
    private Long cacheMaximumSize;

    @Value("${decode.cache.expireAfterWrite}")
    private Long cacheExpireAfterWrite;

    @Value("${decode.cache.level}")
    private int cacheLevel;

    private TimeUnit timeUnit = TimeUnit.MINUTES;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initialize() throws ExecutionException {
        String msg;
        this.decodeIdentifierCache = CacheBuilder.newBuilder()
                .maximumSize(getMaximumSize())
                .expireAfterWrite(getExpireAfterWrite(), timeUnit)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String identifier) {
                        logger.info("IdentifierCache: not found record with identifier=" + identifier
                                + " ... Loading data ...");
                        return getIdentifierCacheRecord(identifier);
                    }
                });
        msg = "IdentifierCache: create with maximumSize=" + getMaximumSize() + " object, expireAfterWrite=" +
                getExpireAfterWrite() + " " + timeUnit.name().toLowerCase();
        logger.info(msg);

        switch (cacheLevel) {
            case 1:
                //TODO refactoring:upload all system`s identifiers
                System[] identifierSystemList = System.values();
                for (int i = 1; i <= identifierSystemList.length - 1; i++) {
                    if (identifierSystemList[i].name() != "UNRECOGNIZED" &&
                            identifierSystemList[i].name() != "UNKNOWN_SYSTEM")
                        this.decodeIdentifierCache.get(identifierSystemList[i].name());
                }
                //TODO refactoring:upload all catalog`s identifiers
                Catalog[] identifierCatalogList = Catalog.values();
                for (int i = 1; i <= identifierCatalogList.length - 1; i++) {
                    if (identifierCatalogList[i].name() != "UNRECOGNIZED" &&
                            identifierCatalogList[i].name() != "UNKNOWN_CATALOG")
                        this.decodeIdentifierCache.get(identifierCatalogList[i].name());
                }
                break;
            default:
                break;
        }

        this.decodeRecordCache = CacheBuilder.newBuilder()
                .maximumSize(getMaximumSize())
                .expireAfterWrite(getExpireAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<DecodeRecord>>() {
                    @Override
                    public List<DecodeRecord> load(String identifier) throws Exception {
                        logger.info("TableCache: not found record with identifier=" + identifier
                                + " ... Loading data ...");
                        return getTableCacheRecord(identifier);
                    }
                });
        msg = "TableCache: create with maximumSize=" + getMaximumSize() + " object, expireAfterWrite=" +
                getExpireAfterWrite() + " " + timeUnit.name().toLowerCase();
        logger.info(msg);
    }

    private List<DecodeRecord> getTableCacheRecord(String identifier) throws ExecutionException {
        List<DecodeRecord> result = new ArrayList<>();
        String targetValueSQL = "";
        RecordRowMapper rowMapper = new RecordRowMapper();
        try {
            result = jdbcTemplate.query(targetValueSQL, new Object[]{}, rowMapper);
        } finally {
            decodeRecordCache.put(identifier, result);
            String msg = "Out getValue with decodeCache size = " + decodeRecordCache.size() + " and DecodeRecordUID = "
                    + identifier;
            logger.info(msg);
        }

        return result;
    }

    private String getIdentifierCacheRecord(String identifier) {
        String result = null;
        String sql = null;
        String msg = null;

        System[] systemName = System.values();
        List<String> s = new ArrayList<>();
        for (int i = 1; i <= systemName.length - 1; i++) {
            s.add(systemName[i].name());
        }

        try {
            if (s.contains(identifier)) {
                sql = selectSystemIdentifier.replace("?", String.valueOf(identifier));
            } else {
                sql = selectCatalogIdentifier.replace("?", String.valueOf(identifier));
            }
            try {
                result = jdbcTemplate.queryForObject(sql, String.class);
                decodeIdentifierCache.put(identifier, result);
            } catch (EmptyResultDataAccessException e) {
                msg = "IdentifierCache: not found record with identifier=" + identifier + " in DB. "
                        + e.getMessage();
                logger.error(msg);
                result = "";
            } finally {
                msg = "IdentifierCache: success add {identifier=" + identifier +
                        "; value=" + result + "}, cache size=" + decodeIdentifierCache.size();
                logger.info(msg);
            }
        } catch (Exception e) {
            msg = "IdentifierCache: error queryForObject with SQL: " + sql + ". " + e.getMessage();
            logger.error(msg);
        }
        return result;
    }

    private Long getMaximumSize() {
        return cacheMaximumSize;
    }

    private Long getExpireAfterWrite() {
        return cacheExpireAfterWrite;
    }

    public List<DecodeRecord> getValueFromCache(String param) throws ExecutionException {
        return decodeRecordCache.get(param);
    }
}
