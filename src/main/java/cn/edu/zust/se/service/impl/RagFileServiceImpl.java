package cn.edu.zust.se.service.impl;

import cn.edu.zust.se.config.EmbeddingConfig;
import cn.edu.zust.se.config.LLMConfig;
import cn.edu.zust.se.domain.PageDTO;
import cn.edu.zust.se.domain.po.RagFile;
import cn.edu.zust.se.domain.query.RagFileQuery;
import cn.edu.zust.se.domain.vo.RagFileVO;
import cn.edu.zust.se.mapper.RagFileMapper;
import cn.edu.zust.se.domain.TestSource;
import cn.edu.zust.se.service.RagFileServiceI;
import cn.edu.zust.se.util.MinioUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.spi.data.document.parser.DocumentParserFactory;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.spi.ServiceHelper.loadFactories;

/**
 * @author anymore131
 * @since 2025-03-23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagFileServiceImpl extends ServiceImpl<RagFileMapper, RagFile> implements RagFileServiceI {
    private final LLMConfig llmConfig;
    private final RagFileMapper ragFileMapper;
    private final EmbeddingConfig embeddingConfig;
    private final MinioUtil minioUtil;

    @Override
    public Integer load(Long ragId, MultipartFile... files) {
        if (files == null){
            throw new RuntimeException("文件为空！");
        }
        int sum = 0;
        EmbeddingModel embeddingModel = llmConfig.OllamaEmbeddingModel("http://localhost:11434", "mxbai-embed-large:latest");
        EmbeddingStore<TextSegment> embeddingStore = embeddingConfig.EmbeddingStore(ragId);
        for (MultipartFile file : files){
            if (file.isEmpty()) {
                sum++;
                continue;
            }
            String fileName = file.getName();
            try (InputStream inputStream = file.getInputStream()) {
                Document document = fileLoad(inputStream, fileName);
                EmbeddingStoreIngestor.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .documentSplitter(new DocumentByLineSplitter(100, 30))
                        .build().ingest(document);
            } catch (IOException e) {
                throw new RuntimeException("文件解析失败！");
            }
            String uuidFileName = minioUtil.upload(file);
            RagFile ragFile = new RagFile();
            ragFile.setRagId(ragId);
            ragFile.setFileName(file.getOriginalFilename());
            String preview = minioUtil.preview(uuidFileName, file.getOriginalFilename());
            int index = preview.indexOf("?");
            ragFile.setFileContent(preview.substring(0, index));
            ragFile.setCreateTime(LocalDateTime.now());
            save(ragFile);
        }
        return sum;
    }

    @Override
    public List<RagFileVO> getFiles(Long ragId) {
        List<RagFile> list = lambdaQuery().eq(RagFile::getRagId, ragId).list();
        if (list != null && !list.isEmpty()) {
            return BeanUtil.copyToList(list, RagFileVO.class);
        }
        return List.of();
    }

    @Override
    public PageDTO<RagFileVO> adminPageByQuery(RagFileQuery query) {
        Page<RagFileVO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<RagFileVO> iPage = ragFileMapper.adminPageByQuery(page, query);
        if (iPage == null){
            return PageDTO.empty(0L,0L);
        }
        PageDTO<RagFileVO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        pageDTO.setPages(iPage.getPages());
        pageDTO.setList(iPage.getRecords());
        return pageDTO;
    }

    // 重写文件加载
    private Document fileLoad(InputStream inputStream, String fileName){
        TestSource testSource = new TestSource(inputStream, fileName);
        return DocumentLoader.load(testSource, getOrDefault(loadDocumentParser(), TextDocumentParser::new));
    }

    // 源码上扒下来的
    static DocumentParser loadDocumentParser() {
        var factories = loadFactories(DocumentParserFactory.class);

        if (factories.size() > 1) {
            throw new RuntimeException("Conflict: multiple document parsers have been found in the classpath. "
                    + "Please explicitly specify the one you wish to use.");
        }

        for (DocumentParserFactory factory : factories) {
            return factory.create();
        }

        return null;
    }
}
