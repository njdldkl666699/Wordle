import json


# 解析单词表文件并将其转换为JSON格式
def parse_wordlist(input_file, output_file):
    result = []
    with open(input_file, "r", encoding="utf-8") as file:
        content = file.read()
        # 按空行分割成单词块
        word_blocks = content.split("\n\n")

        for block in word_blocks:
            lines = block.strip().split("\n")
            if len(lines) < 3:  # 如果块中行数不足3行，跳过
                continue

            word_line = lines[0].strip()  # 单词行
            meaning_line = lines[2].strip()  # 含义行

            # 跳过没有音标或没有释义的单词
            if not word_line or not meaning_line:
                continue

            # 提取单词（去掉序号）
            word = word_line.split()[0]
            # 提取含义
            meaning = meaning_line

            result.append({"word": word, "meaning": meaning})

    # 将结果写入JSON文件
    with open(output_file, "w", encoding="utf-8") as json_file:
        json.dump(result, json_file, ensure_ascii=False, indent=4)


# 解析json，并进行过滤
# 只保留字母数在4~11的单词
def json_filter(input_file, output_file):
    with open(input_file, "r", encoding="utf-8") as file:
        data = json.load(file)
        filtered_data = [entry for entry in data if 4 <= len(entry["word"]) <= 11]

    # 将过滤后的结果写入JSON文件
    with open(output_file, "w", encoding="utf-8") as json_file:
        json.dump(filtered_data, json_file, ensure_ascii=False, indent=4)


# 把过滤后的json按字母数（4~11）分组
def group_by_length(input_file, output_file):
    with open(input_file, "r", encoding="utf-8") as file:
        data = json.load(file)
        grouped_data = {str(i): [] for i in range(4, 12)}
        for entry in data:
            length = len(entry["word"])
            if 4 <= length <= 11:
                grouped_data[str(length)].append(entry)

    # 将分组后的结果写入JSON文件
    with open(output_file, "w", encoding="utf-8") as json_file:
        json.dump(grouped_data, json_file, ensure_ascii=False, indent=4)


if __name__ == "__main__":
    input_file = "30k-explained.txt"  # 输入的单词表文件路径
    intermediate_file = "wordlist.json"  # 中间JSON文件路径
    filtered_file = "filtered_wordlist.json"  # 过滤后的JSON文件路径
    grouped_file = "grouped_wordlist.json"  # 分组后的JSON文件路径

    # 解析单词表并生成JSON文件
    parse_wordlist(input_file, intermediate_file)
    # 过滤JSON文件
    json_filter(intermediate_file, filtered_file)
    # 分组JSON文件
    group_by_length(filtered_file, grouped_file)
