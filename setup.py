from setuptools import find_packages
from setuptools import setup

# packages go here
REQUIRED_PACKAGES = []

setup(
    name='trainer',
    version='0.1',
    install_requires=REQUIRED_PACKAGES,
    packages=find_packages(),
    include_package_data=True,
    description='Training application package.'
)

# run `python setup.py sdist` to create package
