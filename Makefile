.DEFAULT_GOAL := all
.PHONY: all help

GIT_BINARY := $(shell command -v git 2>/dev/null)
MAVEN_BINARY := $(shell command -v mvn 2>/dev/null)

MAVEN_OPTS ?= --illegal-access=warn
# For spotbugs
MAVEN_OPTS := $(MAVEN_OPTS) --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED

all: dist

help: ## Displays this message
	@grep -E '^[A-Za-z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: deps
deps: ## Ensures required dependencies are met
ifndef GIT_BINARY
	$(error "Install git to continue.")
endif
ifndef MAVEN_BINARY
	$(error "Install Apache Maven to continue.")
endif

.PHONY: version
version: deps ## Identifies the version of this codebase
	@$(GIT_BINARY) rev-parse HEAD

.PHONY: dist
dist: deps ## Builds the image
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) package

.PHONY: check
check: deps ## Runs all the tests for the codebase
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) verify

.PHONY: deploy
deploy: deps ## Deploys the artifact
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) deploy

.PHONY: install
install: deps ## Installs the image in local Maven repository
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) install

.PHONY: installcheck
installcheck: ## Runs non-regression tests for the codebase
	$(info "No regression tests exist")

.PHONY: release
release: deps ## Releases and deploys the image to ECR
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) clean release:prepare
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) release:perform

.PHONY: clean
clean: deps ## Cleans up the local environment
	@MAVEN_OPTS="$(MAVEN_OPTS)" $(MAVEN_BINARY) $(MAVEN_ARGS) clean

.PHONY: mostlyclean
mostlyclean: deps ## Cleans up most .gitignore files and directories
	@$(GIT_BINARY) clean -fXd

.PHONY: distclean
distclean: deps ## Cleans up all .gitignore files and directories
	@$(GIT_BINARY) clean -fXdf

.PHONY: maintainer-clean
maintainer-clean: deps ## Cleans up all untracked files and directories
	@$(GIT_BINARY) clean -fxdf
