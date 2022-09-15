# -*- coding: utf-8 -*-

import os
import re
import tarfile

version_matrix = [('2.11', '2.4.8'), ('2.12', '2.4.8'), ('2.12', '3.3.0'), ('2.13', '3.3.0')]


def version():
    f = open('build.sbt', 'r')
    version = ''
    for line in f.readlines():
        match_version = re.match(r'.*\s*version\s*:=\s*"(.+)"\s*', line)
        if match_version:
            version = match_version.group(1)
    f.close()
    return version


def pack(scala_version, spark_version):
    # read build.sbt contents
    f = open('build.sbt', 'r')
    sbt_contents = f.readlines()
    f.close()

    # change contents to pack version
    new_contents = []
    for line in sbt_contents:
        if line.startswith('lazy val packScalaVersion ='):
            new_contents.append('lazy val packScalaVersion = "%s.8"\n' % scala_version)
        elif line.startswith('lazy val packSparkVersion ='):
            new_contents.append('lazy val packSparkVersion = "%s"\n' % spark_version)
        else:
            new_contents.append(line)

    # write new contents to build.sbt
    f = open('build.sbt', 'w')
    new_contents = "".join(new_contents)
    f.write(new_contents)
    f.close()

    pack_info = os.popen('sbt ++%s pack' % scala_version).readlines()
    print("".join(pack_info))

    # recover build.sbt
    f = open('build.sbt', 'w')
    f.write("".join(sbt_contents))
    f.close()
    return pack_info


def pack_success(pack_info):
    for line in pack_info:
        if line.startswith('[success] Total time'):
            return True
    return False


def inject():
    script = open('target/pack/bin/sdoob', 'r')
    contents = script.readlines()
    script.close()

    injected = '. "${PROG_HOME}"/bin/load-spark-env.sh\n'

    index = contents.index('PROG_NAME=sdoob\n')

    contents.insert(index + 3, '\n')
    contents.insert(index + 4, injected)

    script = open('target/pack/bin/sdoob', 'w', newline='\n')
    contents = "".join(contents)
    script.write(contents)
    script.close()


if __name__ == '__main__':
    version = version()
    for (scala, spark) in version_matrix:
        file_name = 'target/sdoob-%s-scala_%s-spark_%s.tar.gz' % (version, scala, spark)
        if os.path.exists(file_name):
            os.remove(file_name)
        print("packing %s" % file_name)
        pack_info = pack(scala, spark)
        if pack_success(pack_info):
            inject()
            with tarfile.open(file_name, 'w:gz') as tar:
                tar.add('target/pack', arcname=os.path.basename('sdoob'))
        else:
            print("error with packing %s:" % file_name)
