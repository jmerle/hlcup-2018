# Highload Cup 2018
My solution to the [Highload Cup 2018](https://highloadcup.ru/en/main/).

## Scripts
- `scripts/submit.sh` can be used to easily submit a new solution.
- `scripts/test.sh` can be used to test against test data. The test data is not in the `.gitignore` to make it easier to use and re-produce wrong results. Requires [Conda](https://conda.io/docs/) to be installed. Their is a hard-coded path to Conda's functions file, so if you're seeing an error make sure that path is correct.
- `scripts/run.sh` can be used to run the application like it is ran in `scripts/test.sh`. This script will also make the application load the data from `tests/data`, just like the testing script does.
