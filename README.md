# PsFmExtractor

![Build Status](https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=KH_PsFmExtractor)

A variability-model extractor for [KernelHaven](https://github.com/KernelHaven/KernelHaven).

An extractor that parses `.xfm` files.

## Usage

Place [`PsFmExtractor.jar`](https://jenkins-2.sse.uni-hildesheim.de/job/KH_PsFmExtractor/lastSuccessfulBuild/artifact/build/jar/PsFmExtractor.jar) in the plugins folder of KernelHaven.

To use this extractor, set `variability.extractor.class` to `net.ssehub.kernel_haven.psfm_extractor.TODO` in the KernelHaven properties.

## Dependencies

This plugin has no additional dependencies other than KernelHaven.

## License

This plugin is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).
